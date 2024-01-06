package test.java;

import main.java.com.precisiontestautomation.automation.utils.KeyInitializers;

public class CustomClass {

    public String body(){
        return "PrecisionTestAutomation1";
    }

    public void LoadString(){
        KeyInitializers.getDriver().get().get("https://www.amazon.in/");
    }

    public void fetchBody(){
        System.out.println(KeyInitializers.getGlobalVariables().get().get("TestName1"));
    }

    public void getEmail(){
        System.out.println(KeyInitializers.getGlobalVariables().get());
    }
}
