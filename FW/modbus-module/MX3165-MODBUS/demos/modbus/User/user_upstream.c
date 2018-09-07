#include "mico.h"
#include "fog_v2_include.h"
#include "user_upstream.h"
#include "user_device_control.h"
#include "user_uart_data_process.h"

//�����ƶ˱������ݵ�ʱ��������ʱ�����õ�ʱ����min
#define STORE_DATA_TIME_INTERVAL   5
//���嶨ʱ����ʱ1min
#define TIMER_1MIN                 (60*1000)
//�ƶ��Ƿ񱣴����ݵı�־λ
//volatile unsigned char IsStoreData = 0;

//��һ���ϴ�������Ҫ����
//volatile unsigned char Is_First_Upload_Sucess = 0;
//��ʱ��
//mico_timer_t store_data_timer;


int upload_try_times = 0;

extern mico_queue_t modbus_real_data_queue;



//��ʱ���ص����� 1mim��ʱ��
/*
void storedata_timer_callback(void *arg)
{
    static uint32_t timer_interrupt_times = 0;
    timer_interrupt_times += 1;
    if(is_http_get_dr_cmd == 1)
    {
        dr_upload_data_time_control += 1;
        if(dr_upload_data_time_control >= DR_UPLOAD_DATA_TIME_INTERVAL)
        {
            is_http_get_dr_cmd = 0;
            dr_upload_data_time_control = 0;
        }
    }
    
    
    //user_modbus_log("come in storedata_timer_callback %d times",timer_interrupt_times);
    if(timer_interrupt_times >= STORE_DATA_TIME_INTERVAL)
    {
        IsStoreData = 1;       
        //user_modbus_log("timer %d min come",timer_interrupt_times);
        timer_interrupt_times = 0;
        
    }   
    mico_rtos_start_timer(&store_data_timer);
}
*/

extern FOG_DES_S *fog_des_g;
extern modbus_devs_t modbus_devs;

extern  OSStatus modbus_device_relation_event(device_t **devices, uint8_t count, int32_t *relation_code);

extern OSStatus modbus_fog_v2_device_send_event(const char *device_id, const char *payload, uint32_t flag);
 
void user_upstream_thread ( mico_thread_arg_t arg ) {
  
  device_t *p = NULL;
  uint8_t i = 0;
//  char *pstring = "{\"key%d\":%d}";
//  char json_data[100] = { 0 };
  int rc = -1;
  int32_t relation_code = -1;  
  wifi_to_mcu_cmd_t wifi_to_mcu_cmd = {0}; 
  uint8_t *databuff = NULL;
  OSStatus err = kUnknownErr;
  wifi_uart_rx_msg_t *real_data_msg = NULL;
  char *json_data = NULL;
  uint8_t json_length = 0;
  uint8_t slave = 0;
  uint8_t wifi_res_data_length = 0;
  
  
  mico_Context_t *mico_context = ( mico_Context_t * ) arg;
  require ( mico_context, exit );
  
  wifi_to_mcu_cmd.func = 0x14;
  
  mico_thread_sleep(5);
  
get_modbus_device_num:
  databuff = malloc(10);
  err = wifi_send_data_to_mcu_event(&wifi_to_mcu_cmd,databuff,10,&wifi_res_data_length);
  if(err != 0)
  {
      user_upstream_log("get modbus device num fail");
      free(databuff);
      mico_thread_sleep(5);
      goto get_modbus_device_num;
  }
  else
  {
      modbus_devs.dev_nums = databuff[3];
      user_upstream_log("get modbus device num success,num = %d",modbus_devs.dev_nums);
  }  
  free(databuff);
  //mico_thread_sleep(5);
  //goto get_modbus_device_num;
 
#ifdef USE_MODBOS_DEVICES
  //modbus_devs.dev_nums = 3;
  p = malloc(modbus_devs.dev_nums * sizeof(device_t));
  for(i = 0;i < modbus_devs.dev_nums;i++)
  {
      modbus_devs.devices[i] = p + i;
      user_upstream_log("%d devices addr:%p",i,modbus_devs.devices[i]);
      memset(modbus_devs.devices[i],0,sizeof(device_t));
      modbus_devs.devices[i]->num = i;
      sprintf(modbus_devs.devices[i]->device_mac,"%s%02x",fog_des_g->device_mac,i);
  }
  
  while(!modbus_devs.is_all_devs_activate)
  {
      for(i = 0;i < modbus_devs.dev_nums;i++)
      {
          if(modbus_devs.devices[i]->is_activate == 0)
          {
              modbus_devices_activate(modbus_devs.devices[i]);
              mico_thread_sleep(1);
              break;
          }
      }
      
      if(i == modbus_devs.dev_nums)
      {
          modbus_devs.is_all_devs_activate = 1;
      }
  }
  
device_relation:
  if(modbus_devs.is_all_devs_sub == 0)
  {
      mico_thread_sleep(1);
      goto device_relation;
  }
  rc = modbus_device_relation_event(modbus_devs.devices,modbus_devs.dev_nums,&relation_code);
  if(rc != 0)
  {
      mico_thread_sleep(5);
      goto device_relation;
  }
  
#endif

  
  while ( 1 ) {
    
    if(modbus_devs.is_all_devs_activate == 0 || modbus_devs.is_all_devs_sub == 0)
    {
        mico_thread_sleep(1);
        continue;
    }
    
//    for(i = 0;i < modbus_devs.dev_nums;i++)
//    {
//        memset(json_data,0,strlen(json_data));
//        sprintf(json_data,pstring,i,i);  
//          
//        upload_dev_data(modbus_devs.devices[i]->device_id,json_data,0);
//        mico_thread_sleep(1);
//    }
    
    if(mico_rtos_is_queue_empty(modbus_real_data_queue) == false)
    {
        err = mico_rtos_pop_from_queue(&modbus_real_data_queue, &real_data_msg, 10);
        if(err != 0)
        {
            continue;
        }
        
        //user_upstream_log("pop one msg from real_data_queue");
        //uart_printf_array("msg",real_data_msg->data_buff,real_data_msg->length);
        
        if(real_data_msg == NULL || real_data_msg->data_buff == NULL || real_data_msg->length <= 7)
        {
            goto free_msg;
        }
        
        
        if(real_data_msg->data_buff[0] == 0x2A && real_data_msg->data_buff[2] == 0x01)
        {
            slave = real_data_msg->data_buff[3];
            if(slave >= modbus_devs.dev_nums)
            {
                goto free_msg;
            }
            
            json_length = real_data_msg->length - 7;
            json_data = malloc(json_length + 1);
            if(json_data == NULL)
            {
                user_upstream_log("malloc json_data fail");
                goto free_msg;
            }
            memset(json_data,0,json_length + 1);
            memcpy(json_data,real_data_msg->data_buff + 4,json_length);
            //uart_printf_array("json",(uint8_t *)json_data,json_length);
            if(json_data[0] == '{' && json_data[json_length - 1] == '}')
            {
                user_upstream_log("upload json data:%s",json_data);
                modbus_fog_v2_device_send_event(modbus_devs.devices[slave]->device_id,json_data,0);
            }
            else
            {
                user_upstream_log("json payload form error");
            }
            
            free(json_data);
            json_data = NULL;
            
        }

    free_msg:        
        if(real_data_msg != NULL)
        {
            free(real_data_msg);
            real_data_msg = NULL;
        }
        
        
    }
    
    
    mico_thread_msleep(10);

    
  }
  
exit:
  if ( kNoErr != err ) {
    user_upstream_log ( "ERROR: user_upstream_thread exit with err=%d", err );
  }
  if(p != NULL)
  {
      free(p);
  }
  
  mico_rtos_delete_thread ( NULL );
}





OSStatus upload_dev_data (const char *device_id,char *json_string,uint8_t save_flag) {
  
  OSStatus err = kNoErr;
  
  if(json_string == NULL)
  {
    err = kGeneralErr;
    goto exit;
  }
  
  if(fog_v2_is_https_connect() == true && get_wifi_status() == true){
    user_modbus_log("json_string:%s", json_string);
    
    err = modbus_fog_v2_device_send_event(device_id, json_string, save_flag);   
    
    if(kNoErr != err){
      user_modbus_log("ERROR: Upload data failed, Err=%d", err);
      upload_try_times += 1 ;
      goto exit;
    }else{
      upload_try_times = 0 ;
    }
  }  
  else {
      user_modbus_log("ERROR: Upload data failed, because WIFI or http disconnect!");
      upload_try_times += 1;
  }
  
 exit:
   
  //rebooting  for mutl upload failed
  if(upload_try_times > UPLOAD_TRY_COUNT){
    user_modbus_log("ERROR: Mico reboot because multiple upload failed!");
    MicoSystemReboot();
  }
  return err;
}


/*
OSStatus upload_dev_data (char *json_string,uint8_t save_flag) {
  
  OSStatus err = kNoErr;
  
  if(json_string == NULL)
  {
    err = kGeneralErr;
    goto exit;
  }
  
  if(fog_v2_is_https_connect() == true && get_wifi_status() == true){
    user_modbus_log("json_string:%s", json_string);
    
    err = fog_v2_device_send_event(json_string, save_flag);   
    
    if(kNoErr != err){
      user_modbus_log("ERROR: Upload data failed, Err=%d", err);
      upload_try_times += 1 ;
      goto exit;
    }else{
      upload_try_times = 0 ;
    }
  }  
  else {
      user_modbus_log("ERROR: Upload data failed, because WIFI or http disconnect!");
      upload_try_times += 1;
  }
  
 exit:
   
  //rebooting  for mutl upload failed
  if(upload_try_times > UPLOAD_TRY_COUNT){
    user_modbus_log("ERROR: Mico reboot because multiple upload failed!");
    MicoSystemReboot();
  }
  return err;
}
*/




