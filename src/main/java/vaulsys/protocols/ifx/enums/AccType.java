package vaulsys.protocols.ifx.enums;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AccType implements Cloneable, Serializable {

	private static final int MAIN_ACCOUNT_VALUE = 0;
	private static final int SUBSIDIARY_ACCOUNT_VALUE = 1;
	private static final int CARD_VALUE = 2;

	//m.rehman: changing the values according to ISO Account Types
	/*
	private static final int SAVING_VALUE =  12;
	private static final int CURRENT_VALUE = 11;
	*/
	private static final int SAVING_VALUE =  10;
	private static final int CURRENT_VALUE = 20;
	private static final int CREDIT_VALUE = 30;
	private static final int UNIVERSAL_VALUE = 40;
	private static final int DEPOSIT_VALUE = 13;
	
	private static final int UNKNOWN_VALUE = -1;
	
	private static final int TARJIHI_VALUE = 3;//for saderat

	public static final int WALLET_VALUE = 50; //Raza adding for NayaPay
	public static final int LEVEL_ZERO_VALUE = 0; //Raza adding for NayaPay segregating from above main_account value
	public static final int LEVEL_ONE_VALUE = 1; //Raza adding for NayaPay segregating from above subsidiary_account value
	public static final int LEVEL_TWO_VALUE = 2; //Raza adding for NayaPay segregating from above main_account value
	public static final int MERCHANT_WALLET_VALUE = 3;	//m.rehman: for merchant wallet
	public static final int PREPAID_WALLET_VALUE = 4;	//m.rehman: for prepaid wallet

	public static final String CAT_PROV_VALUE = "PROV";
	public static final String CAT_LINKED_VALUE = "LINK";
	public static final String CAT_WALLET_VALUE = "WLLT";
	public static final String CAT_MERCHANT_WALLET_VALUE = "MER_WLLT";
	public static final String CAT_PREPAID_WALLET_VALUE = "PRE_WLLT";

	//Added by m.waleed 06/09/20233 VC-NAP-202303062 - Merchant Portal
	//Added by m.waleed 06/09/20233 VC-NAP-202303062 - Merchant Portal
	public static final String CAT_MERCHANT_REV_WALLET_VALUE = "MER_REV_WLLT";
	public static final String CAT_MERCHANT_EXP_WALLET_VALUE = "MER_EXP_WLLT";
	// ==========================================================

	public static final AccType CAT_MERCHANT_REV_WALLET =  new AccType(CAT_MERCHANT_REV_WALLET_VALUE);
	public static final AccType CAT_MERCHANT_EXP_WALLET =  new AccType(CAT_MERCHANT_EXP_WALLET_VALUE);
	//================================================================

	public static final String CAT_BILLER_WLLT_VALUE = "BILLER_WLLT";
	public static final String CAT_SETTLEMENT_WLLT_VALUE = "SETT_WLLT";
	public static final String CAT_SALESTAX_WLLT_VALUE = "TAX_WLLT";
	public static final String CAT_ARC_SALESTAX_WLLT_VALUE = "ARC_TAX_WLLT";//ADDED BY WALEED
	public static final String CAT_ARC_REVENUE_WLLT_VALUE = "ARC_REVENUE_WLLT";//ADDED BY WALEED
	public static final String CAT_REVENUE_WLLT_VALUE = "REVENUE_WLLT";
	public static final String CAT_MERCHANT_SETTLEMENT_WALLET_VALUE = "MER_SETT_WLLT";
	public static final String CAT_AGNT_VALUE = "AGNT_WLLT";
	public static final String CAT_AGNT_COMM_VALUE = "AGNT_COMM_WLLT";
	public static final String CAT_1LINK_SETT_WLLT_VALUE = "1LINK_SETT_WLLT";
	public static final String CAT_PARTNER_BANK_SETT_WLLT_VALUE = "PARTNER_BANK_SETT_WLLT";

	public static final String CAT_SETT_ACCT_VALUE = "SETT_ACCT";
	public static final String CAT_REVENUE_ACCT_VALUE = "REVENUE_ACCT";
	public static final String CAT_BILLER_ACCT_VALUE = "BILLER_ACCT";
	public static final String CAT_LIABILITY_ACCT_VALUE = "LIABILITY_ACCT";
	public static final String CAT_ARC_LIABILITY_ACCT_VALUE = "ARC_LIABILITY_ACCT"; //Added by Waleed
	public static final String CAT_1LINK_SETT_ACCT_VALUE = "1LINK_SETT_ACCT";
	public static final String CAT_PARTNER_BANK_SETT_ACCT_VALUE = "PARTNER_BANK_SETT_ACCT";

	// Asim Shahzad, Date : 25th June 2020, Desc : Added Merchant Settlement account in emi_account_collection
	public static final String CAT_MERCHANT_SETTLEMENT_ACCT_VALUE = "MER_SETT_ACCT";


	// Asim Shahzad, Date : 24th Sep 2020, Tracking ID : VP-NAP-202009081/VC-NAP-202009081
	public static final String CAT_1LINK_SETT_PAYABLE_ACCT_VALUE = "1LINK_SETT_ACCT_PAYABLE";
	public static final String CAT_1LINK_SETT_RECEIVABLE_ACCT_VALUE = "1LINK_SETT_ACCT_REC";
	public static final String CAT_1LINK_SETT_PAYABLE_WLLT_VALUE = "1LINK_SETT_WLLT_PAYABLE";
	public static final String CAT_1LINK_SETT_RECEIVABLE_WLLT_VALUE = "1LINK_SETT_WLLT_RECEIVABLE";
	// ===================================================================================

	// Asim Shahzad, Date : 29th Sep 2020, Tracking ID : VC-NAP-202009253
	public static final String CAT_SUSPENSE_ACCT_VALUE = "SUSPENSE_ACCT";
	public static final String CAT_SUSPENSE_WLLT_VALUE = "SUSPENSE_WLLT";
	// ===================================================================================

	//m.rehman: Euronet Integration
	private static final String CAT_VISA_INTL_SETT_PAYABLE_WLLT_VALUE = "VISA_INTL_SETT_PAYABLE_WLLT";
	private static final String CAT_VISA_INTL_SETT_RECEIVABLE_WLLT_VALUE = "VISA_INTL_SETT_RECEIVABLE_WLLT";
	private static final String CAT_VISA_LCL_SETT_PAYABLE_WLLT_VALUE = "VISA_LCL_SETT_PAYABLE_WLLT";
	private static final String CAT_VISA_LCL_SETT_RECEIVABLE_WLLT_VALUE = "VISA_LCL_SETT_RECEIVABLE_WLLT";
	private static final String CAT_WITHHOLDING_INCOME_TAX_WLLT_VALUE = "WITHHOLDING_INCOME_TAX_WLLT";
	private static final String CAT_1LINK_VISA_SETT_PAYABLE_WLLT_VALUE = "1LINK_VISA_SETT_PAYABLE_WLLT";
	private static final String CAT_1LINK_VISA_SETT_RECEIVABLE_WLLT_VALUE = "1LINK_VISA_SETT_RECEIVABLE_WLLT";
	private static final String CAT_PREAUTH_SNDRY_WLLT_VALUE = "PREAUTH_SNDRY_WLLT";
	private static final String CAT_VISA_INTL_SETT_PAYABLE_ACCT_VALUE = "VISA_INTL_SETT_PAYABLE_ACCT";
	private static final String CAT_VISA_INTL_SETT_RECEIVABLE_ACCT_VALUE = "VISA_INTL_SETT_RECEIVABLE_ACCT";
	private static final String CAT_VISA_LCL_SETT_PAYABLE_ACCT_VALUE = "VISA_LCL_SETT_PAYABLE_ACCT";
	private static final String CAT_VISA_LCL_SETT_RECEIVABLE_ACCT_VALUE = "VISA_LCL_SETT_RECEIVABLE_ACCT";
	private static final String CAT_WITHHOLDING_INCOME_TAX_ACCT_VALUE = "WITHHOLDING_INCOME_TAX_ACCT";
	private static final String CAT_1LINK_VISA_SETT_PAYABLE_ACCT_VALUE = "1LINK_VISA_SETT_PAYABLE_ACCT";
	private static final String CAT_1LINK_VISA_SETT_RECEIVABLE_ACCT_VALUE = "1LINK_VISA_SETT_RECEIVABLE_ACCT";
	private static final String CAT_PREAUTH_SNDRY_ACCT_VALUE = "PREAUTH_SNDRY_ACCT";
	/////////////////////////////////////////////////////////////////////////////////////////////////////////

	//m.rehman: 05-04-2021, VP-NAP-202103292 / VC-NAP-202103293 - Refund Module Part 2
	private static final String CAT_DISPUTE_SETT_RECEIVABLE_WLLT_VALUE = "DISPUTE_SETT_RECEIVABLE_WLLT";
	private static final String CAT_DISPUTE_SETT_PAYABLE_WLLT_VALUE = "DISPUTE_SETT_PAYABLE_WLLT";
	private static final String CAT_DISPUTE_SETT_RECEIVABLE_ACCT_VALUE = "DISPUTE_SETT_RECEIVABLE_ACCT";
	private static final String CAT_DISPUTE_SETT_PAYABLE_ACCT_VALUE = "DISPUTE_SETT_PAYABLE_ACCT";
	private static final String CAT_SUSPENSE_RECEIVABLE_WLLT_VALUE = "CAT_SUSPENSE_RECEIVABLE_WLLT";
	private static final String CAT_SUSPENSE_PAYABLE_WLLT_VALUE = "CAT_SUSPENSE_PAYABLE_WLLT";
	private static final String CAT_SUSPENSE_RECEIVABLE_ACCT_VALUE = "CAT_SUSPENSE_RECEIVABLE_ACCT";
	private static final String CAT_SUSPENSE_PAYABLE_ACCT_VALUE = "CAT_SUSPENSE_PAYABLE_ACCT";
	//////////////////////////////////////////////////////////////////////////////////

	// Asim Shahzad, Date : 27th Jan 2022, Tracking ID : VP-NAP-202111122 / VC-NAP-202111122
	public static final String CAT_VISA_SUSPENSE_ACCT_VALUE = "VISA_SUSPENSE_ACCT";
	public static final String CAT_VISA_SUSPENSE_WLLT_VALUE = "VISA_SUSPENSE_WLLT";
	// =====================================================================================

	// Asim Shahzad, Date : 9th Sep 2022, Tracking ID : NAP-R4-22
	public static final String CAT_VISA_LOCAL_REFUND_PAYABLE_ACCT_VALUE = "VISA_LOCAL_REFUND_PAYABLE_ACCT";
	public static final String CAT_VISA_LOCAL_REFUND_PAYABLE_WLLT_VALUE = "VISA_LOCAL_REFUND_PAYABLE_WLLT";

	public static final String CAT_VISA_INT_REFUND_PAYABLE_ACCT_VALUE = "VISA_INT_REFUND_PAYABLE_ACCT";
	public static final String CAT_VISA_INT_REFUND_PAYABLE_WLLT_VALUE = "VISA_INT_REFUND_PAYABLE_WLLT";
	// ==========================================================


	// Asim Shahzad, Date : 5th Feb 2023, Tracking ID : VP-NAP-202302021 / VC-NAP-202302021
	public static final String CAT_FOREX_BUFFER_DCC_ACCT_VALUE = "FOREX_BUFFER_DCC_ACCT";
	public static final String CAT_FOREX_BUFFER_DCC_WLLT_VALUE = "FOREX_BUFFER_DCC_WLLT";
	public static final String CAT_FOREX_BUFFER_NON_DCC_ACCT_VALUE = "FOREX_BUFFER_NON_DCC_ACCT";
	public static final String CAT_FOREX_BUFFER_NON_DCC_WLLT_VALUE = "FOREX_BUFFER_NON_DCC_WLLT";
	public static final String CAT_VISA_SURCHARGE_ACCT_VALUE = "VISA_SURCHARGE_ACCT";
	public static final String CAT_VISA_SURCHARGE_WLLT_VALUE = "VISA_SURCHARGE_WLLT";
	public static final String CAT_VISA_SURCHARGE_FOREX_BUFFER_ACCT_VALUE = "VISA_SURCHARGE_FOREX_BUFFER_ACCT";
	public static final String CAT_VISA_SURCHARGE_FOREX_BUFFER_WLLT_VALUE = "VISA_SURCHARGE_FOREX_BUFFER_WLLT";
	public static final String CAT_INTL_TXN_FEE_ACCT_VALUE = "INTL_TXN_FEE_ACCT";
	public static final String CAT_INTL_TXN_FEE_WLLT_VALUE = "INTL_TXN_FEE_WLLT";
	// ==================================================================

	// Asim Shahzad, Date : 16 Nov 2023, Tracking ID : VC-NAP-202303062
	private static final String CAT_ARC_VISA_LCL_SETT_PAYABLE_ACCT_VALUE = "ARC_VISA_LCL_SETT_PAYABLE_ACCT";
	private static final String CAT_ARC_VISA_LCL_SETT_PAYABLE_WLLT_VALUE = "ARC_VISA_LCL_SETT_PAYABLE_WLLT";
	private static final String CAT_ARC_VISA_INTL_SETT_PAYABLE_ACCT_VALUE = "ARC_VISA_INTL_SETT_PAYABLE_ACCT";
	private static final String CAT_ARC_VISA_INTL_SETT_PAYABLE_WLLT_VALUE = "ARC_VISA_INTL_SETT_PAYABLE_WLLT";
	public static final String CAT_ARC_REVENUE_ACCT_VALUE = "ARC_REVENUE_ACCT";
	private static final String CAT_ARC_WITHHOLDING_INCOME_TAX_WLLT_VALUE = "ARC_WITHHOLDING_INCOME_TAX_WLLT";
	private static final String CAT_ARC_WITHHOLDING_INCOME_TAX_ACCT_VALUE = "ARC_WITHHOLDING_INCOME_TAX_ACCT";
	public static final String CAT_ARC_INTL_TXN_FEE_WLLT_VALUE = "ARC_INTL_TXN_FEE_WLLT";
	public static final String CAT_ARC_INTL_TXN_FEE_ACCT_VALUE = "ARC_INTL_TXN_FEE_ACCT";
	public static final String CAT_ARC_FOREX_BUFFER_DCC_WLLT_VALUE = "ARC_FOREX_BUFFER_DCC_WLLT";
	public static final String CAT_ARC_FOREX_BUFFER_DCC_ACCT_VALUE = "ARC_FOREX_BUFFER_DCC_ACCT";
	public static final String CAT_ARC_FOREX_BUFFER_NON_DCC_WLLT_VALUE = "ARC_FOREX_BUFFER_NON_DCC_WLLT";
	public static final String CAT_ARC_FOREX_BUFFER_NON_DCC_ACCT_VALUE = "ARC_FOREX_BUFFER_NON_DCC_ACCT";
	private static final String CAT_ARC_PREAUTH_SNDRY_ACCT_VALUE = "ARC_PREAUTH_SNDRY_ACCT";
	private static final String CAT_ARC_PREAUTH_SNDRY_WLLT_VALUE = "ARC_PREAUTH_SNDRY_WLLT";


	//added by waleed
	public static final String CAT_ARC_VISA_INTL_CASH_WDW_FEE_WLLT_VALUE = "ARC_VISA_INTL_CASH_WDW_FEE_WLLT";
	public static final String CAT_ARC_VISA_INTL_CASH_WDW_FEE_ACCT_VALUE = "ARC_VISA_INTL_CASH_WDW_FEE_ACCT";

	public static final String CAT_ARC_VISA_INTL_BAL_INQ_CHARGES_WLLT_VALUE = "ARC_VISA_INTL_BAL_INQ_CHARGES_WLLT";
	public static final String CAT_ARC_VISA_INTL_BAL_INQ_CHARGES_ACCT_VALUE = "ARC_VISA_INTL_BAL_INQ_CHARGES_ACCT";


	public static final String CAT_ARC_VISA_SURCHARGE_ACCT_VALUE = "ARC_VISA_SURCHARGE_ACCT";
	public static final String CAT_ARC_VISA_SURCHARGE_WLLT_VALUE = "ARC_VISA_SURCHARGE_WLLT";
	public static final String CAT_ARC_VISA_SURCHARGE_FOREX_BUFFER_ACCT_VALUE = "ARC_VISA_SURCHARGE_FOREX_BUFFER_ACCT";
	public static final String CAT_ARC_VISA_SURCHARGE_FOREX_BUFFER_WLLT_VALUE = "ARC_VISA_SURCHARGE_FOREX_BUFFER_WLLT";


	//added by waleed
	public static final String CAT_ARC_VISA_LOCAL_REFUND_PAYABLE_ACCT_VALUE = "ARC_VISA_LOCAL_REFUND_PAYABLE_ACCT";
	public static final String CAT_ARC_VISA_LOCAL_REFUND_PAYABLE_WLLT_VALUE = "ARC_VISA_LOCAL_REFUND_PAYABLE_WLLT";

	public static final String CAT_ARC_VISA_INT_REFUND_PAYABLE_ACCT_VALUE = "ARC_VISA_INT_REFUND_PAYABLE_ACCT";
	public static final String CAT_ARC_VISA_INT_REFUND_PAYABLE_WLLT_VALUE = "ARC_VISA_INT_REFUND_PAYABLE_WLLT";

	// ================================================================
	
	public static final AccType MAIN_ACCOUNT = new AccType(MAIN_ACCOUNT_VALUE);
	public static final AccType SUBSIDIARY_ACCOUNT = new AccType(SUBSIDIARY_ACCOUNT_VALUE);
	public static final AccType CARD = new AccType(CARD_VALUE);

	public static final AccType SAVING =  new AccType(SAVING_VALUE);
	public static final AccType DEPOSIT = new AccType(DEPOSIT_VALUE);
	public static final AccType CURRENT = new AccType(CURRENT_VALUE);
	public static final AccType UNKNOWN = new AccType(UNKNOWN_VALUE);
	public static final AccType CREDIT = new AccType(CREDIT_VALUE);
	public static final AccType UNIVERSAL = new AccType(UNIVERSAL_VALUE);
	
	public static final AccType TARJIHI =  new AccType(TARJIHI_VALUE);//for saderat

	public static final AccType WALLET =  new AccType(WALLET_VALUE); //Raza for NayaPay
	public static final AccType LEVEL_ZERO =  new AccType(LEVEL_ZERO_VALUE); //Raza for NayaPay
	public static final AccType LEVEL_ONE =  new AccType(LEVEL_ONE_VALUE); //Raza for NayaPay
	public static final AccType LEVEL_TWO =  new AccType(LEVEL_TWO_VALUE); //Raza for NayaPay
	public static final AccType MERCHANT_WALLET = new AccType(MERCHANT_WALLET_VALUE);
	public static final AccType PREPAID_WALLET = new AccType(PREPAID_WALLET_VALUE);





	public static final AccType CAT_PROV =  new AccType(CAT_PROV_VALUE); //Raza for NayaPay
	public static final AccType CAT_LINKED =  new AccType(CAT_LINKED_VALUE); //Raza for NayaPay
	public static final AccType CAT_WALLET =  new AccType(CAT_WALLET_VALUE); //Raza for NayaPay
	public static final AccType CAT_MERCHANT_WALLET =  new AccType(CAT_MERCHANT_WALLET_VALUE);
	public static final AccType CAT_PREPAID_WALLET =  new AccType(CAT_PREPAID_WALLET_VALUE);

	public static final AccType CAT_AGNT_WALLET =  new AccType(CAT_AGNT_VALUE);
	public static final AccType CAT_AGNT_COMM_WALLET =  new AccType(CAT_AGNT_COMM_VALUE);

	public static final AccType CAT_SETT_WALLET =  new AccType(CAT_SETTLEMENT_WLLT_VALUE);
	public static final AccType CAT_BILLER_WALLET =  new AccType(CAT_BILLER_WLLT_VALUE);
	public static final AccType CAT_SALESTAX_WLLT =  new AccType(CAT_SALESTAX_WLLT_VALUE);
	public static final AccType CAT_ARC_SALESTAX_WLLT =  new AccType(CAT_ARC_SALESTAX_WLLT_VALUE); //added by waleed
	public static final AccType CAT_ARC_REVENUE_WLLT =  new AccType(CAT_ARC_REVENUE_WLLT_VALUE); //added by waleed
	public static final AccType CAT_REVENUE_WLLT =  new AccType(CAT_REVENUE_WLLT_VALUE);
	public static final AccType CAT_MERCHANT_SETTLEMENT_WALLET =  new AccType(CAT_MERCHANT_SETTLEMENT_WALLET_VALUE);
	public static final AccType CAT_SETT_ACCT =  new AccType(CAT_SETT_ACCT_VALUE);
	public static final AccType CAT_REVENUE_ACCT =  new AccType(CAT_REVENUE_ACCT_VALUE);
	public static final AccType CAT_BILLER_ACCT =  new AccType(CAT_BILLER_ACCT_VALUE);
	public static final AccType CAT_LIABILITY_ACCT =  new AccType(CAT_LIABILITY_ACCT_VALUE);
	public static final AccType CAT_1LINK_SETT_ACCT =  new AccType(CAT_1LINK_SETT_ACCT_VALUE);
	public static final AccType CAT_1LINK_SETT_WLLT =  new AccType(CAT_1LINK_SETT_WLLT_VALUE);
	public static final AccType CAT_PARTNER_BANK_SETT_ACCT =  new AccType(CAT_PARTNER_BANK_SETT_ACCT_VALUE);
	public static final AccType CAT_PARTNER_BANK_SETT_WLLT =  new AccType(CAT_PARTNER_BANK_SETT_WLLT_VALUE);

	// Asim Shahzad, Date : 25th June 2020, Desc : Added Merchant Settlement account in emi_account_collection
	public static final AccType CAT_MERCHANT_SETTLEMENT_ACCT =  new AccType(CAT_MERCHANT_SETTLEMENT_ACCT_VALUE);

	// Asim Shahzad, Date : 24th Sep 2020, Tracking ID : VP-NAP-202009081/VC-NAP-202009081
	public static final AccType CAT_1LINK_SETT_PAYABLE_ACCT =  new AccType(CAT_1LINK_SETT_PAYABLE_ACCT_VALUE);
	public static final AccType CAT_1LINK_SETT_RECEIVABLE_ACCT =  new AccType(CAT_1LINK_SETT_RECEIVABLE_ACCT_VALUE);

	public static final AccType CAT_1LINK_SETT_PAYABLE_WLLT =  new AccType(CAT_1LINK_SETT_PAYABLE_WLLT_VALUE);
	public static final AccType CAT_1LINK_SETT_RECEIVABLE_WLLT =  new AccType(CAT_1LINK_SETT_RECEIVABLE_WLLT_VALUE);
	// ===================================================================================

	//added by waleed
	public static final AccType CAT_ARC_VISA_INTL_CASH_WDW_FEE_WLLT = new AccType(CAT_ARC_VISA_INTL_CASH_WDW_FEE_WLLT_VALUE);
	public static final AccType CAT_ARC_VISA_INTL_CASH_WDW_FEE_ACCT = new AccType(CAT_ARC_VISA_INTL_CASH_WDW_FEE_ACCT_VALUE);

	public static final AccType CAT_ARC_VISA_INTL_BAL_INQ_CHARGES_WLLT = new AccType(CAT_ARC_VISA_INTL_BAL_INQ_CHARGES_WLLT_VALUE);
	public static final AccType CAT_ARC_VISA_INTL_BAL_INQ_CHARGES_ACCT = new AccType(CAT_ARC_VISA_INTL_BAL_INQ_CHARGES_ACCT_VALUE);


	public static final AccType CAT_ARC_VISA_SURCHARGE_ACCT =  new AccType(CAT_ARC_VISA_SURCHARGE_ACCT_VALUE);
	public static final AccType CAT_ARC_VISA_SURCHARGE_WLLT =  new AccType(CAT_ARC_VISA_SURCHARGE_WLLT_VALUE);
	public static final AccType CAT_ARC_VISA_SURCHARGE_FOREX_BUFFER_ACCT =  new AccType(CAT_ARC_VISA_SURCHARGE_FOREX_BUFFER_ACCT_VALUE);
	public static final AccType CAT_ARC_VISA_SURCHARGE_FOREX_BUFFER_WLLT =  new AccType(CAT_ARC_VISA_SURCHARGE_FOREX_BUFFER_WLLT_VALUE);
	// ================================================================

	// Asim Shahzad, Date : 29th Sep 2020, Tracking ID : VC-NAP-202009253
	public static final AccType CAT_SUSPENSE_WLLT =  new AccType(CAT_SUSPENSE_WLLT_VALUE);
	public static final AccType CAT_SUSPENSE_ACCT =  new AccType(CAT_SUSPENSE_ACCT_VALUE);
	// ==================================================================

	//m.rehman: Euronet Integration
	public static final AccType CAT_VISA_INTL_SETT_PAYABLE_ACCT =  new AccType(CAT_VISA_INTL_SETT_PAYABLE_ACCT_VALUE);
	public static final AccType CAT_VISA_INTL_SETT_PAYABLE_WLLT =  new AccType(CAT_VISA_INTL_SETT_PAYABLE_WLLT_VALUE);
	public static final AccType CAT_VISA_INTL_SETT_RECEIVABLE_ACCT =  new AccType(CAT_VISA_INTL_SETT_RECEIVABLE_ACCT_VALUE);
	public static final AccType CAT_VISA_INTL_SETT_RECEIVABLE_WLLT =  new AccType(CAT_VISA_INTL_SETT_RECEIVABLE_WLLT_VALUE);
	public static final AccType CAT_VISA_LCL_SETT_PAYABLE_ACCT =  new AccType(CAT_VISA_LCL_SETT_PAYABLE_ACCT_VALUE);
	public static final AccType CAT_VISA_LCL_SETT_PAYABLE_WLLT =  new AccType(CAT_VISA_LCL_SETT_PAYABLE_WLLT_VALUE);
	public static final AccType CAT_VISA_LCL_SETT_RECEIVABLE_ACCT =  new AccType(CAT_VISA_LCL_SETT_RECEIVABLE_ACCT_VALUE);
	public static final AccType CAT_VISA_LCL_SETT_RECEIVABLE_WLLT =  new AccType(CAT_VISA_LCL_SETT_RECEIVABLE_WLLT_VALUE);
	public static final AccType CAT_1LINK_VISA_SETT_PAYABLE_ACCT =  new AccType(CAT_1LINK_VISA_SETT_PAYABLE_ACCT_VALUE);
	public static final AccType CAT_1LINK_VISA_SETT_PAYABLE_WLLT =  new AccType(CAT_1LINK_VISA_SETT_PAYABLE_WLLT_VALUE);
	public static final AccType CAT_1LINK_VISA_SETT_RECEIVABLE_ACCT =  new AccType(CAT_1LINK_VISA_SETT_RECEIVABLE_ACCT_VALUE);
	public static final AccType CAT_1LINK_VISA_SETT_RECEIVABLE_WLLT =  new AccType(CAT_1LINK_VISA_SETT_RECEIVABLE_WLLT_VALUE);
	public static final AccType CAT_WITHHOLDING_INCOME_TAX_ACCT =  new AccType(CAT_WITHHOLDING_INCOME_TAX_ACCT_VALUE);
	public static final AccType CAT_WITHHOLDING_INCOME_TAX_WLLT =  new AccType(CAT_WITHHOLDING_INCOME_TAX_WLLT_VALUE);
	public static final AccType CAT_PREAUTH_SNDRY_WLLT =  new AccType(CAT_PREAUTH_SNDRY_WLLT_VALUE);
	public static final AccType CAT_PREAUTH_SNDRY_ACCT =  new AccType(CAT_PREAUTH_SNDRY_ACCT_VALUE);
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//m.rehman: 05-04-2021, VP-NAP-202103292 / VC-NAP-202103293 - Refund Module Part 2
	public static final AccType CAT_DISPUTE_SETT_RECEIVABLE_ACCT = new AccType(CAT_DISPUTE_SETT_RECEIVABLE_ACCT_VALUE);
	public static final AccType CAT_DISPUTE_SETT_PAYABLE_ACCT = new AccType(CAT_DISPUTE_SETT_PAYABLE_ACCT_VALUE);
	public static final AccType CAT_DISPUTE_SETT_RECEIVABLE_WLLT = new AccType(CAT_DISPUTE_SETT_RECEIVABLE_WLLT_VALUE);
	public static final AccType CAT_DISPUTE_SETT_PAYABLE_WLLT = new AccType(CAT_DISPUTE_SETT_PAYABLE_WLLT_VALUE);
	public static final AccType CAT_SUSPENSE_RECEIVABLE_ACCT = new AccType(CAT_SUSPENSE_RECEIVABLE_ACCT_VALUE);
	public static final AccType CAT_SUSPENSE_PAYABLE_ACCT = new AccType(CAT_SUSPENSE_PAYABLE_ACCT_VALUE);
	public static final AccType CAT_SUSPENSE_RECEIVABLE_WLLT = new AccType(CAT_SUSPENSE_RECEIVABLE_WLLT_VALUE);
	public static final AccType CAT_SUSPENSE_PAYABLE_WLLT = new AccType(CAT_SUSPENSE_PAYABLE_WLLT_VALUE);
	//////////////////////////////////////////////////////////////////////////////////

	// Asim Shahzad, Date : 27th Jan 2022, Tracking ID : VP-NAP-202111122 / VC-NAP-202111122
	public static final AccType CAT_VISA_SUSPENSE_ACCT =  new AccType(CAT_VISA_SUSPENSE_ACCT_VALUE);
	public static final AccType CAT_VISA_SUSPENSE_WLLT =  new AccType(CAT_VISA_SUSPENSE_WLLT_VALUE);
	// =====================================================================================

	// Asim Shahzad, Date : 9th Sep 2022, Tracking ID : NAP-R4-22
	public static final AccType CAT_VISA_LOCAL_REFUND_PAYABLE_ACCT =  new AccType(CAT_VISA_LOCAL_REFUND_PAYABLE_ACCT_VALUE);
	public static final AccType CAT_VISA_LOCAL_REFUND_PAYABLE_WLLT =  new AccType(CAT_VISA_LOCAL_REFUND_PAYABLE_WLLT_VALUE);

	public static final AccType CAT_VISA_INT_REFUND_PAYABLE_ACCT =  new AccType(CAT_VISA_INT_REFUND_PAYABLE_ACCT_VALUE);
	public static final AccType CAT_VISA_INT_REFUND_PAYABLE_WLLT =  new AccType(CAT_VISA_INT_REFUND_PAYABLE_WLLT_VALUE);
	// ==========================================================

	// Asim Shahzad, Date : 5th Feb 2023, Tracking ID : VP-NAP-202302021 / VC-NAP-202302021
	public static final AccType CAT_FOREX_BUFFER_DCC_ACCT =  new AccType(CAT_FOREX_BUFFER_DCC_ACCT_VALUE);
	public static final AccType CAT_FOREX_BUFFER_DCC_WLLT =  new AccType(CAT_FOREX_BUFFER_DCC_WLLT_VALUE);
	public static final AccType CAT_FOREX_BUFFER_NON_DCC_ACCT =  new AccType(CAT_FOREX_BUFFER_NON_DCC_ACCT_VALUE);
	public static final AccType CAT_FOREX_BUFFER_NON_DCC_WLLT =  new AccType(CAT_FOREX_BUFFER_NON_DCC_WLLT_VALUE);
	public static final AccType CAT_VISA_SURCHARGE_ACCT =  new AccType(CAT_VISA_SURCHARGE_ACCT_VALUE);
	public static final AccType CAT_VISA_SURCHARGE_WLLT =  new AccType(CAT_VISA_SURCHARGE_WLLT_VALUE);
	public static final AccType CAT_VISA_SURCHARGE_FOREX_BUFFER_ACCT =  new AccType(CAT_VISA_SURCHARGE_FOREX_BUFFER_ACCT_VALUE);
	public static final AccType CAT_VISA_SURCHARGE_FOREX_BUFFER_WLLT =  new AccType(CAT_VISA_SURCHARGE_FOREX_BUFFER_WLLT_VALUE);
	public static final AccType CAT_INTL_TXN_FEE_ACCT =  new AccType(CAT_INTL_TXN_FEE_ACCT_VALUE);
	public static final AccType CAT_INTL_TXN_FEE_WLLT =  new AccType(CAT_INTL_TXN_FEE_WLLT_VALUE);
	public static final AccType CAT_ARC_PREAUTH_SNDRY_ACCT =  new AccType(CAT_ARC_PREAUTH_SNDRY_ACCT_VALUE);
	// ==================================================================

	// Asim Shahzad, Date : 16 Nov 2023, Tracking ID : VC-NAP-202303062
	public static final AccType CAT_ARC_VISA_LCL_SETT_PAYABLE_ACCT =  new AccType(CAT_ARC_VISA_LCL_SETT_PAYABLE_ACCT_VALUE);
	public static final AccType CAT_ARC_VISA_LCL_SETT_PAYABLE_WLLT =  new AccType(CAT_ARC_VISA_LCL_SETT_PAYABLE_WLLT_VALUE);
	public static final AccType CAT_ARC_VISA_INTL_SETT_PAYABLE_ACCT =  new AccType(CAT_ARC_VISA_INTL_SETT_PAYABLE_ACCT_VALUE);
	public static final AccType CAT_ARC_VISA_INTL_SETT_PAYABLE_WLLT =  new AccType(CAT_ARC_VISA_INTL_SETT_PAYABLE_WLLT_VALUE);
	public static final AccType CAT_ARC_REVENUE_ACCT =  new AccType(CAT_ARC_REVENUE_ACCT_VALUE);
	public static final AccType CAT_ARC_LIABILITY_ACCT =  new AccType(CAT_ARC_LIABILITY_ACCT_VALUE);
	public static final AccType CAT_ARC_WITHHOLDING_INCOME_TAX_WLLT =  new AccType(CAT_ARC_WITHHOLDING_INCOME_TAX_WLLT_VALUE);
	public static final AccType CAT_ARC_WITHHOLDING_INCOME_TAX_ACCT =  new AccType(CAT_ARC_WITHHOLDING_INCOME_TAX_ACCT_VALUE);
	public static final AccType CAT_ARC_INTL_TXN_FEE_WLLT =  new AccType(CAT_ARC_INTL_TXN_FEE_WLLT_VALUE);
	public static final AccType CAT_ARC_INTL_TXN_FEE_ACCT =  new AccType(CAT_ARC_INTL_TXN_FEE_ACCT_VALUE);
	public static final AccType CAT_ARC_FOREX_BUFFER_DCC_WLLT =  new AccType(CAT_ARC_FOREX_BUFFER_DCC_WLLT_VALUE);
	public static final AccType CAT_ARC_FOREX_BUFFER_DCC_ACCT =  new AccType(CAT_ARC_FOREX_BUFFER_DCC_ACCT_VALUE);
	public static final AccType CAT_ARC_FOREX_BUFFER_NON_DCC_WLLT =  new AccType(CAT_ARC_FOREX_BUFFER_NON_DCC_WLLT_VALUE);
	public static final AccType CAT_ARC_FOREX_BUFFER_NON_DCC_ACCT =  new AccType(CAT_ARC_FOREX_BUFFER_NON_DCC_ACCT_VALUE);
	public static final AccType CAT_ARC_PREAUTH_SNDRY_WLLT =  new AccType(CAT_ARC_PREAUTH_SNDRY_WLLT_VALUE);

	//AddedByWaleed
	public static final AccType CAT_ARC_VISA_LOCAL_REFUND_PAYABLE_ACCT =  new AccType(CAT_ARC_VISA_LOCAL_REFUND_PAYABLE_ACCT_VALUE);
	public static final AccType CAT_ARC_VISA_LOCAL_REFUND_PAYABLE_WLLT =  new AccType(CAT_ARC_VISA_LOCAL_REFUND_PAYABLE_WLLT_VALUE);

	public static final AccType CAT_ARC_VISA_INT_REFUND_PAYABLE_ACCT =  new AccType(CAT_ARC_VISA_INT_REFUND_PAYABLE_ACCT_VALUE);
	public static final AccType CAT_ARC_VISA_INT_REFUND_PAYABLE_WLLT =  new AccType(CAT_ARC_VISA_INT_REFUND_PAYABLE_WLLT_VALUE);
	// ================================================================
	
	private int type;
	private String category; //Raza adding for NayaPay (Provisional, Linked, Wallet)

	public AccType() {
	}

	public AccType(int type) {
		super();
		this.type = type;
	}

	public AccType(String cat) {
		super();
		this.category = cat;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCategory() { return category; }

	public void setCategory(String cat) { this.category = cat; }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		AccType that = (AccType) obj;
		return type == that.type;
	}

	@Override
	public int hashCode() {
		return type;
	}
	
	@Override
	protected Object clone() {
		return new AccType(this.type); 
	}
	
	public AccType copy() {
		return (AccType) clone();
	}
	
	@Override
	public String toString() {
		return type + "";
	}


	public String StringValue(){ return category+"";}

	//m.rehman: mapping accoutn type
	public static AccType mapAcctType (String inAcctType) throws Exception {
		AccType outAcctType;
		try {
			switch (Integer.parseInt(inAcctType)) {
				case MAIN_ACCOUNT_VALUE:
					outAcctType = MAIN_ACCOUNT;
					break;
				case SAVING_VALUE:
					outAcctType = SAVING;
					break;
				case CURRENT_VALUE:
					outAcctType = CURRENT;
					break;
				case UNIVERSAL_VALUE:
					outAcctType = UNIVERSAL;
					break;
				default:
					outAcctType = null;
					break;
			}
		} catch (Exception e) {
			throw e;
		}

		return outAcctType;
	}
}
