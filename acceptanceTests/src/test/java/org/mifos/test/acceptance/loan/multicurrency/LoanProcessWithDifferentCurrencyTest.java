package org.mifos.test.acceptance.loan.multicurrency;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.loanproduct.multicurrrency.DefineNewDifferentCurrencyLoanProductPage.SubmitMultiCurrencyFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = { "smoke", "loanproduct", "acceptance" })
public class LoanProcessWithDifferentCurrencyTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    private LoanTestHelper loanTestHelper;
    
    private CustomPropertiesHelper propertiesHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        
        propertiesHelper = new CustomPropertiesHelper(selenium);
        propertiesHelper.setAdditionalCurrenciesCode("USD");
        
        String testDataSet = "acceptance_small_001_dbunit.xml.zip";
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, testDataSet, dataSource, selenium);
        
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009, 7, 11, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    @AfterMethod
    public void logOut() {
        propertiesHelper.setAdditionalCurrenciesCode("");
        initRemote.reinitializeApplication(selenium);
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void loanProcessWithDifferentCurrency() throws Exception {
        createWeeklyLoanProduct();
        newWeeklyClientLoanAccount();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    private void createWeeklyLoanProduct() throws Exception {
        SubmitMultiCurrencyFormParameters formParameters = getWeeklyLoanProductParameters();
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        adminPage.defineMultiCurrencyLoanProduct(formParameters);

    }

    @SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "unused" })
    // one of the dependent methods throws Exception
    private void newWeeklyClientLoanAccount() throws Exception {
        loanTestHelper = new LoanTestHelper(selenium);
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Veronica Abisya");
        searchParameters.setLoanProduct("Loan With Different Currency");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1012.0");

        createLoanAndCheckAmount(searchParameters, submitAccountParameters);
    }

    private void createLoanAndCheckAmount(CreateLoanAccountSearchParameters searchParameters,
            CreateLoanAccountSubmitParameters submitAccountParameters) {
        LoanAccountPage loanAccountPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        loanAccountPage.verifyLoanAmount(submitAccountParameters.getAmount());
    }

    private SubmitMultiCurrencyFormParameters getWeeklyLoanProductParameters() {
        SubmitMultiCurrencyFormParameters formParameters = new SubmitMultiCurrencyFormParameters();
        formParameters.setOfferingName("Loan With Different Currency");
        formParameters.setOfferingShortName("DC" + StringUtil.getRandomString(2));
        formParameters.setDescription("descriptionForWeekly1");
        formParameters.setCategory("Other");
        formParameters.setApplicableFor(SubmitFormParameters.CLIENTS);
        formParameters.setMinLoanAmount("1000");
        formParameters.setMaxLoanAmount("19000");
        formParameters.setDefaultLoanAmount("2500");
        formParameters.setInterestTypes(SubmitFormParameters.DECLINING_BALANCE);
        formParameters.setMaxInterestRate("10");
        formParameters.setMinInterestRate("6");
        formParameters.setDefaultInterestRate("9");
        formParameters.setFreqOfInstallments(SubmitFormParameters.WEEKS); 
        formParameters.setMaxInstallments("10");
        formParameters.setDefInstallments("5");
        formParameters.setGracePeriodType(SubmitFormParameters.NONE);
        formParameters.setInterestGLCode("31102");
        formParameters.setPrincipalGLCode("1506");
        formParameters.setCurrencyId(Short.valueOf("1"));
        return formParameters;
    }

    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials().navigateToAdminPage();
    }

}
