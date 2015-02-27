package com.suhaib.flappycheatNhack;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/** @author Kevin Kowalewski */
public class Root {

    public boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2();
    }

      public boolean checkRootMethod1() {
        try {
            File file = new File("/system/app/Superuser.apk");
            return file.exists();
        } catch (Exception e) {
        	return false;
        }
    }
    
    public boolean checkRootMethod2() {
    	Process p;
		try{
			// Preform su to get root privledges
			p = Runtime.getRuntime().exec("su");
			// Attempt to write a file to a root-only
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("mount -o rw,remount /system\n");
			os.writeBytes("echo \"Do I have root?\" >/system/etc/temporary.txt\n");
			// Close the terminal
			os.writeBytes("exit\n");
			os.flush();
			try{
				p.waitFor();
				if(p.exitValue() != 225){
					return true;
				} else {
					return false;
				}
			} catch(InterruptedException e){
				return false;
			}
		} catch(IOException e){
			return false;
		}
    }

}   

