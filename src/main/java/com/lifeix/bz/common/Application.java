package com.lifeix.bz.common;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.lifeix.football.common.ApplicationUtil;

/**
 * 
 * @author zengguangwei
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ApplicationUtil.run(Application.class, args);
	}

}
