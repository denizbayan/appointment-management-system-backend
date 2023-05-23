package com.appointmentManagementSystem.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class UserSockets {

   public static HashMap<Long,HashMap<String,Long>> userSockets = new HashMap<>();
   public static HashMap<Long,HashMap<String,Long>> roomProperties = new HashMap<>();


}
