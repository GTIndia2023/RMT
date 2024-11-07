package RMT.Pages;

import RMT.Constants.AppConstants;
import RMT.Utils.ElementUtil;
import RMT.Utils.TimeUtil;
import org.openqa.selenium.WebDriver;

public class SkillMasterPage {
    private WebDriver driver;
    ElementUtil eleutil;

    //1. Skill master page constructor
    public SkillMasterPage(WebDriver driver){
        this.driver=driver;
        eleutil=new ElementUtil(driver);
    }


    //3.Page actions

    public String getSkillMasterPageTitle() throws InterruptedException {
        String title=eleutil.waitForTitleToBe(AppConstants.SKILL_MASTER_PAGE_TITLE, TimeUtil.MEDIUM_TIME_OUT);
        System.out.println("Skill master Page  title is "+ title);
        return title;
    }
    public String getSkillMasterPageUrl(){
        String url=eleutil.waitForURLToBe(AppConstants.SKILL_MASTER_PAGE_URL,TimeUtil.DEFAULT_TIME_OUT);
        System.out.println("Skill master page Url is " + url);
        return url;
    }
}
