/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package foodcenter.android.service;

/**
 * Class to be customized with app-specific data. The Eclipse plugin will set
 * these values when the project is created.
 */
public class Setup
{

	/**
	 * The AppEngine app name, used to construct the production service URL
	 * below.
	 */
	private static final String APP_NAME = "food-center";

	/**
	 * The URL of the production service.
	 */
	public static final String PROD_URL = "https://" + APP_NAME + ".appspot.com";
	
	 /**
     * Intent used to display a message in the screen.
     */
	public static final String DISPLAY_MESSAGE_ACTION = "foodcenter.android.DISPLAY_MESSAGE";
	  /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";
    
}
