package vaulsys.othermains.disagreement;

import vaulsys.authorization.policy.Bank;
import vaulsys.calendar.DateTime;
import vaulsys.calendar.DayDate;
import vaulsys.calendar.DayTime;
import vaulsys.clearing.AccountingService;
import vaulsys.clearing.base.SettlementReport;
import vaulsys.clearing.report.SorushRecord;
import vaulsys.customer.Core;
import vaulsys.exception.SorushDisagreementException;
import vaulsys.persistence.GeneralDao;
import vaulsys.protocols.ifx.enums.IfxDirection;
import vaulsys.protocols.ifx.enums.IfxType;
import vaulsys.protocols.ifx.enums.TrnType;
import vaulsys.protocols.ifx.imp.Ifx;
import vaulsys.protocols.PaymentSchemes.base.ISOResponseCodes;
import vaulsys.transaction.LifeCycle;
import vaulsys.transaction.Transaction;
import vaulsys.transaction.TransactionType;
import vaulsys.util.NotUsed;
import vaulsys.util.Pair;
import vaulsys.util.StringFormat;
import vaulsys.util.Util;
import vaulsys.wfe.GlobalContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import org.apache.log4j.Logger;

import com.fanap.cms.exception.BusinessException;
import com.fanap.cms.valueobjects.corecommunication.DocumentItemEntity;
import com.fanap.cms.valueobjects.corecommunication.IssueGeneralDocVO;
import com.ghasemkiani.util.icu.PersianDateFormat;

//TASK Task115 : Pasargad Sorush Disagreement (new feild in sorush disagreemnt)
//TASK Task115 : (3628) SorushDisagreement Pasargad UI
//TASK Task116 : (3771) Change Query for SorushDisagreement
public class SorushDisagreementParallelWithSanadPasargad_ver2 {
	private static final String IncrementalTransfer = "INCREMENTALTRANSFER";
	private static final String BalanceInquery = "BALANCEINQUIRY";

	private static final String ERROR_005 = "فایل ورودی وجود ندارد";
	private static final String ERROR_004 = "محتویات فایل ورودی بانک و شتاب تاریخ های %s مشکل دارد.";
	private static final String ERROR_003 = "فایل ورودی بانک و شتاب فاقد تاریخهای %s است.";
	private static final String ERROR_002 = "فایل زیپ ورودی صحیح نیست";
	private static final String ERROR_001 = "فرمت نام فایل ورودی اشتباه است.";

	public static final class InputType {
		public static final Integer SORUSH_RES = 0;  // File res pishfarz
		public static final Integer SORUSH_RES_AFTER_SANAD = 1; // khoroji filehaye sanad_result ra be onvane vorodi bedahim
		public static final Integer SORUSH = 2;
	}

	public static final class RunningMode {
		public static final Integer MAIN = 0;  // File res pishfarz
		public static final Integer UI = 1; // khoroji filehaye sanad_result ra be onvane vorodi bedahim
	}

	public static final class DataInputType {
		public static final Integer ZIP_FILE = 0;
		public static final Integer FOLDER = 1;
	}

	private static Integer INPUT_TYPE = InputType.SORUSH_RES; //AldComment Add 93.03.09 //baraye inke vorodi jadid biayad in type ra ezafe shod
	private static Boolean GET_DATE_FROM_FILENAME = false; //AldComment

	private static Boolean createXMLDocument = true;
	private static Boolean doSanad = false;
	private static Integer RUNNING_MODE = RunningMode.UI;
	private static Integer DATA_INPUT_TYPE = DataInputType.ZIP_FILE;
	private static Logger logger = Logger.getLogger(SorushDisagreementParallelWithSanadPasargad_ver2.class);

	//***********

	//*********** Pasargad Config
		private static String BESTANKARANE_MOGHAYERATE_SHETAB = "1-995-2F22727-IRR-1";  //بستانکاران داخلي/بستانکاران داخلي-مغايرت شتاب
		private static String VASETE_SHETAB = "1-995-2F206511-IRR-1"; // حساب مقابل حساب واسط ساير سوييچ ها/حساب مقابل واسطه شتاب - پذيرندگان
		private static String MABEOTAFAVOT = "1-995-2F22726-IRR-1";  // بستانکاران داخلي/بستانکاران داخلي-مغايرت اسناد تسويه سوئيچ
		private static String DAKHELI_SHETAB = "1-995-4704-IRR-1";  // داخلی شتاب 

		private static String FANAP_SWITCH_ID = "995";

		private static Integer SANAD_THREAD_COUNT = 8;
		private static Integer READ_FORMS_THREAD_COUNT = 2;
		private static Integer READ_FORMS_THREAD_COUNT_SORUSH = 2;
		private static Integer MAXIMUM_THREAD_FOR_READ_FILE = 2;
		private static String  INPUT_FILES_PATH = null;
		private static String  DATA_PATH = null;
		private static String  OUTPUT_PATH = null;
		private static final int MAXIMUM_TRY_FOR_SANAD = 3;

		private static String _IRI = "iri";
		private static String _ISS = ".iss";
		private static String _ISS_INQ = ".iss.inq";
		private static String _PAS = "PAS";
		private static String _PAS2REP = "PAS2rep";
		private static String _PASREP = "PASrep";
		private static String _SORUSH_PRE_FILENAME = "isspas";


		private static String SHAPARAK_PRE_BIN = "581672";
		private static String SHAPARAK_BIN = "581672000";
		private static Long SORUSH_BIN = 936450L;

		private static String BANK_NAME = "PAS";
		private static String SHETAB_NAME = "pas2-";
		private static String BANK_BIN = "502229";


		private static Long SHAPARAK_ID =  581672L;

		private static Long ISSUER_SHETAB_TERMINAL_ID = 123L; //TASK Task056
		private static final String SHETAB_POSTFIX = "-shetab";
		private static final String SHETAB_POSTFIX_INQ = "-shetab-inq";
		private static final String BANK_POSTFIX = "-bank";
		//*********** End Pasargad Config	

	private static Map<Integer, Long> GetBinByTwoDigit;
	static {
		GetBinByTwoDigit = new HashMap<Integer, Long>();
		GetBinByTwoDigit.put(61, 504706L);
		GetBinByTwoDigit.put(60, 606373L);
		GetBinByTwoDigit.put(16, 639217L);
		GetBinByTwoDigit.put(65, 636949L);
		GetBinByTwoDigit.put(93, 581672000L);
		GetBinByTwoDigit.put(78, 505809L);
		GetBinByTwoDigit.put(15, 589210L);
		GetBinByTwoDigit.put(13, 589463L);
		GetBinByTwoDigit.put(19, 603769L);
		GetBinByTwoDigit.put(16, 603770L);
		GetBinByTwoDigit.put(17, 603799L);
		GetBinByTwoDigit.put(12, 610433L);
		GetBinByTwoDigit.put(56, 621986L);
		GetBinByTwoDigit.put(54, 622106L);
		GetBinByTwoDigit.put(18, 627353L);
		GetBinByTwoDigit.put(55, 627412L);
		GetBinByTwoDigit.put(53, 627488L);
		GetBinByTwoDigit.put(20, 627648L);
		GetBinByTwoDigit.put(21, 627760L);
		GetBinByTwoDigit.put(11, 627961L);
		GetBinByTwoDigit.put(14, 628023L);
		GetBinByTwoDigit.put(57, 502229L);
		GetBinByTwoDigit.put(58, 639607L);
		GetBinByTwoDigit.put(59, 639346L);
		GetBinByTwoDigit.put(51, 628157L);
		GetBinByTwoDigit.put(62, 636214L);
		GetBinByTwoDigit.put(10, 636795L);
		GetBinByTwoDigit.put(52, 639599L);
		GetBinByTwoDigit.put(69, 505785L);
		GetBinByTwoDigit.put(50, 936450L);
		GetBinByTwoDigit.put(66, 502938L);
		GetBinByTwoDigit.put(22, 502908L);
		GetBinByTwoDigit.put(63, 627381L);
		GetBinByTwoDigit.put(64, 505416L);
		GetBinByTwoDigit.put(70, 504172L);
		GetBinByTwoDigit.put(75, 606256L); //موسسه اعتباری عسکریه
		GetBinByTwoDigit.put(73, 505801L); //موسسه اعتباری کوثر

	}

	public static void main3(final String[] args) {
		GeneralDao.Instance.beginTransaction();
		try {
			LifeCycle lifeCycle = new LifeCycle();
			Transaction trxRq = new Transaction();
			Transaction trxRs = new Transaction();
			Ifx ifxRqIn = new Ifx();
			Ifx ifxRqOut = new Ifx();
			Ifx ifxRsIn = new Ifx();
			Ifx ifxRsOut = new Ifx();

			trxRq.setLifeCycle(lifeCycle);
			trxRs.setLifeCycle(lifeCycle);
			ifxRqIn.setIfxType(IfxType.PURCHASE_RQ);
			ifxRqIn.setTrnType(TrnType.PURCHASE);
			ifxRqIn.setTransaction(trxRq);
			ifxRqOut.setIfxType(IfxType.PURCHASE_RQ);
			ifxRqOut.setTrnType(TrnType.PURCHASE);
			ifxRqOut.setTransaction(trxRq);

			ifxRsIn.setIfxType(IfxType.PURCHASE_RS);
			ifxRsIn.setTrnType(TrnType.PURCHASE);
			ifxRsIn.setTransaction(trxRs);
			ifxRsOut.setIfxType(IfxType.PURCHASE_RS);
			ifxRsOut.setTrnType(TrnType.PURCHASE);
			ifxRsOut.setTransaction(trxRs);

			GeneralDao.Instance.saveOrUpdate(trxRq);
			GeneralDao.Instance.saveOrUpdate(trxRs);
			GeneralDao.Instance.saveOrUpdate(lifeCycle);
			GeneralDao.Instance.saveOrUpdate(ifxRqIn);
			GeneralDao.Instance.saveOrUpdate(ifxRqOut);
			GeneralDao.Instance.saveOrUpdate(ifxRsIn);
			GeneralDao.Instance.saveOrUpdate(ifxRsOut);

			System.out.println("Life : " + lifeCycle.getId()
					 +  " trxRq : " + trxRq.getId() + "\r\n"
					 +  " trxRs : " + trxRs.getId() + "\r\n"
					 +  " ifxRqIn : " + ifxRqIn.getId() + "\r\n"
					 +  " ifxRqOut : " + ifxRqOut.getId() + "\r\n"
					 +  " ifxRsIn : " + ifxRsIn.getId() + "\r\n"
					 +  " ifxRsOut : " + ifxRsOut.getId() + "\r\n"
					);

			System.out.println(String.format("delete TRX_LIFECYCLE where id = %s;", lifeCycle.getId())
								 +  "\r\n"
								 +  String.format("delete ifx where id in (%s, %s, %s, %s);", ifxRqIn.getId(), ifxRqOut.getId(), ifxRsIn.getId(), ifxRsOut.getId())
								 +  "\r\n"
								 +  String.format("delete TRX_TRANSAXION where id in (%s, %s);", trxRq.getId(), trxRs.getId())
								 +  "\r\n"
					);
		} finally {
			GeneralDao.Instance.endTransaction();
		}
	}

	public static void test(StringBuilder str) {
		str.delete(0, str.length());
		str.append("vf");
		
	}
	
	public static void main(final String[] args) {
//		StringBuilder str = new StringBuilder("");
//		System.out.println(str);
//		test(str);
//		System.out.println(str);
		/*************************** parsing sorush file for getting workingDay *****************************/
		String path = "E:/ALD/Tasks/Task101 - Resalat SorushDisagreement/nemonefile/isspas920427-1092-21190.txt"; //test sorosh - test100.txt
//		String path="D:/Resalat/SorushDisagreement/issRST930409-1092-19783-2.txt"; //test sorosh - test100.txt
		StringBuilder pathRes = new StringBuilder();
		StringBuilder pathError = new StringBuilder();
		StringBuilder pathDuplicate = new StringBuilder();
		StringBuilder pathProccessBefore = new StringBuilder();
//		String pathRes = path.substring(0, path.length()-4) + "-res.txt";
//		String pathError = path.substring(0, path.length()-4) + "-Error.txt";
//		String pathDuplicate = path.substring(0, path.length()-4) + "-Duplicate.txt";
//		String pathProccessBefore = path.substring(0, path.length()-4) + "-ProccedBefore.txt";
		File firstSorush = new File(path);
		Long myBin = Long.valueOf(BANK_BIN); /*myInstitution.getBin()*/;
		Pair<List<String>, File> result = null;
		try {
			result = new SorushDisagreementParallelWithSanadPasargad_ver2().addWorkingDayToSorushFile(firstSorush, pathRes, pathError, pathDuplicate, pathProccessBefore, myBin, InputType.SORUSH);
		} catch (Exception e) {
			if (e instanceof SorushDisagreementException) {
				System.out.println(e.getMessage());
			}
			e.printStackTrace();
		}
		
		System.out.println(pathRes.toString());
		System.out.println(pathDuplicate.toString());
		System.out.println(pathProccessBefore.toString());
		System.out.println(pathError.toString());
		/***************************** diagreement **********************/

////		String zipPath = "d:/f23.zip";
//		DATA_INPUT_TYPE = DataInputType.FOLDER;
//		RUNNING_MODE = RunningMode.MAIN;
//		String zipPath = "d:/data";
//		String resultPath = "d:/1";
////		File f = new File("e:/test/2/isspas921121-1092-56887-res.txt");
//		File f = new File("E:/ALD/Tasks/Task101 - Resalat SorushDisagreement/nemonefile/issRST920427-1092-21190-res.txt");
//		try {
//			new SorushDisagreementParallelWithSanadPasargad_ver2().findDisagreement(f, zipPath, resultPath, InputType.SORUSH_RES);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			if (e instanceof SorushDisagreementException) {
//				System.out.println(e.getMessage());
//			} else {
//				e.printStackTrace();
//			}
//		}

//		DATA_INPUT_TYPE = DataInputType.FOLDER;
//		String zipPath = "d:/data";
//		String resultPath = "d:/1";
////		File f = new File("D:/1/New folder (4)/r8/final_sanad_with_error_result_isspas930230-1092-11914-res.txt");
//		File f = new File("e:/test/2/isspas921121-1092-56887-res.txt"); //for test
//		try {
//			new SorushDisagreementParallelWithSanadPasargad().findDisagreement(f, zipPath, resultPath,InputType.SORUSH_RES_AFTER_SANAD);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

}

	public static void main2(final String[] args) {

		logger.info("Running Sorush-Disagreement ver 1.6");
		logger.info("Running Sorush-Disagreement ver 1.6");

			/***************************** diagreement **********************/

			String dataPath = null;
			File folder_in = null;
			File configFile = null;

			if (args.length == 1) {
				configFile = new File(args[0]);
			} else {
				logger.error("Wrong Parameters ...");
				logger.info("Wrong Parameters ...");
				return;
			}

			if (!configFile.exists()) {
				logger.info(String.format("config File (%s) not exist !!!", configFile.getAbsolutePath()));
				return;
			}

			Integer res = ReadConfig(configFile);
			if (res.equals(-1)) {
				logger.info("error reading config.txt. exiting main");
				return;
			}

			if (!Util.hasText(INPUT_FILES_PATH)  || !Util.hasText(DATA_PATH)) {
				logger.info("Please set input_files_path and data_path !!!");
				return;
			}

			if (!INPUT_FILES_PATH.endsWith("/")) {
				INPUT_FILES_PATH  += "/";
			}
			if (!DATA_PATH.endsWith("/")) {
				DATA_PATH  += "/";
			}

			folder_in = new File(INPUT_FILES_PATH);
			dataPath = DATA_PATH;
			File folders = new File(DATA_PATH);
			//Initial Check
			//***********
			if (!folder_in.exists()) {
				logger.error(String.format("Input folder (%s) not exist !!!", folder_in.getAbsolutePath()));
				logger.info(String.format("Input folder (%s) not exist !!!", folder_in.getAbsolutePath()));
				return;
			}
			if (!folders.exists()) {
				logger.info(String.format("data folder '%s' not exist !!!", dataPath));
				return;
			}

			//**********

			List<String> checkProcessFileBefore = new ArrayList<String>();
			long t1 = System.currentTimeMillis();

			//********
			logger.info(String.format("input_type_after_sanad : %s", InputType.SORUSH_RES_AFTER_SANAD.equals(INPUT_TYPE) ? "true" : "false"));
			logger.info(String.format("input_type_after_sanad : %s", InputType.SORUSH_RES_AFTER_SANAD.equals(INPUT_TYPE) ? "true" : "false"));
			logger.info(String.format("get_date_from_file_name : %s", GET_DATE_FROM_FILENAME.toString()));
			logger.info(String.format("get_date_from_file_name : %s", GET_DATE_FROM_FILENAME.toString()));

			//*********

			File result = null;
			File[] listOfFiles = folder_in.listFiles();
		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		    	  if (listOfFiles[i].getName().contains("res")) {
		    		  long t2 = System.currentTimeMillis();
		    		  result = listOfFiles[i];
		    		  if (!checkProcessFileBefore.contains(result.getName())) {
			    		  logger.info(String.format("Processing file (%s)", listOfFiles[i].getName()));
			    		  logger.info(String.format("Processing file (%s)", listOfFiles[i].getName()));

		    			  try {
							new SorushDisagreementParallelWithSanadPasargad_ver2().findDisagreement(result, dataPath, OUTPUT_PATH, INPUT_TYPE);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

			    		  logger.info(String.format("total time for processing %s is %s", listOfFiles[i].getName(), (System.currentTimeMillis() - t2)));
			    		  logger.info(String.format("total time for processing %s is %s", listOfFiles[i].getName(), (System.currentTimeMillis() - t2)));
		    		  } else {
			    		  logger.info(String.format("SKIP processing %s because processed before", listOfFiles[i].getName()));
			    		  logger.info(String.format("SKIP processing %s because processed before", listOfFiles[i].getName()));
		    		  }
		    	  }
		      } else if (listOfFiles[i].isDirectory()) {
		        logger.info("Directory " + listOfFiles[i].getName());
		      }
		    }
			logger.info("Total Time : " + (System.currentTimeMillis() - t1));
	}

	@NotUsed
	public static Integer ReadConfig(final File file) {
		String line;
		StringTokenizer tokenizer;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while (br.ready()) {
				if ((line = br.readLine()).length() > 0) {
					tokenizer = new StringTokenizer(line, "=");
					String param = tokenizer.nextToken().trim();
					String value = tokenizer.nextToken().trim();
					if (param.toUpperCase().equals("BESTANKARANE_MOGHAYERATE_SHETAB")) {
						BESTANKARANE_MOGHAYERATE_SHETAB = value;
					}

					if (param.toUpperCase().equals("VASETE_SHETAB")) {
						VASETE_SHETAB = value;
					}

					if (param.toUpperCase().equals("MABEOTAFAVOT")) {
						MABEOTAFAVOT = value;
					}

					if (param.toUpperCase().equals("DAKHELI_SHETAB")) {
						DAKHELI_SHETAB = value;
					}

					if (param.toUpperCase().equals("FANAP_SWITCH_ID")) {
						FANAP_SWITCH_ID = value;
					}

					if (param.toUpperCase().equals("SANAD_THREAD_COUNT")) {
						SANAD_THREAD_COUNT = Integer.valueOf(value);
					}

					if (param.toUpperCase().equals("READ_FORMS_THREAD_COUNT")) {
						READ_FORMS_THREAD_COUNT = Integer.valueOf(value);
					}

					if (param.toUpperCase().equals("READ_FORMS_THREAD_COUNT_SORUSH")) {
						READ_FORMS_THREAD_COUNT_SORUSH = Integer.valueOf(value);
					}

					if (param.toUpperCase().equals("MAXIMUM_THREAD_FOR_READ_FILE")) {
						MAXIMUM_THREAD_FOR_READ_FILE = Integer.valueOf(value);
					}

					if (param.toUpperCase().equals("_IRI")) {
						_IRI = value;
					}

					if (param.toUpperCase().equals("_ISS")) {
						_ISS = value;
					}

					if (param.toUpperCase().equals("_PAS")) {
						_PAS = value;
					}

					if (param.toUpperCase().equals("_PAS2REP")) {
						_PAS2REP = value;
					}

					if (param.toUpperCase().equals("_PASREP")) {
						_PASREP = value;
					}

					if (param.toUpperCase().equals("SHAPARAK_PRE_BIN")) {
						SHAPARAK_PRE_BIN = value;
					}

					if (param.toUpperCase().equals("SHAPARAK_BIN")) {
						SHAPARAK_BIN = value;
					}

					if (param.toUpperCase().equals("SORUSH_BIN")) {
						SORUSH_BIN = Long.valueOf(value);
					}

					if (param.toUpperCase().equals("BANK_NAME")) {
						BANK_NAME = value;
					}

					if (param.toUpperCase().equals("SHETAB_NAME")) {
						SHETAB_NAME = value;
					}

					if (param.toUpperCase().equals("SHAPARAK_ID")) {
						SHAPARAK_ID = Long.valueOf(value);
					}

					if (param.toUpperCase().equals("ISSUER_SHETAB_TERMINAL_ID")) {
						ISSUER_SHETAB_TERMINAL_ID = Long.valueOf(value);
					}

					if (param.toUpperCase().equals("CREATE_XML_DOCUMENT")) {
						createXMLDocument = Boolean.valueOf(value);
					}

					if (param.toUpperCase().equals("DOSANAD")) {
						doSanad = Boolean.valueOf(value);
					}

					if (param.toUpperCase().equals("INPUT_FILES_PATH")) {
						INPUT_FILES_PATH = value;
					}

					if (param.toUpperCase().equals("DATA_PATH")) {
						DATA_PATH = value;
					}

					if (param.toUpperCase().equals("OUTPUT_PATH")) {
						OUTPUT_PATH = value;
					}

					if (param.toUpperCase().equals("INPUT_TYPE_AFTER_SANAD")) {
						if (Boolean.valueOf(value)) {
							INPUT_TYPE = InputType.SORUSH_RES_AFTER_SANAD;
						} else {
							INPUT_TYPE = InputType.SORUSH_RES;
						}
					}

					if (param.toUpperCase().equals("GET_DATE_FROM_FILE_NAME")) {
						GET_DATE_FROM_FILENAME = Boolean.valueOf(value);
					}

				}
			}
			br.close();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public final Pair<List<String>, File> addWorkingDayToSorushFileFromUI( final File sorush, StringBuilder pathRes, StringBuilder pathError, StringBuilder pathDuplicate, StringBuilder pathProccessBefore, final Long myBin, final Integer inputType) throws Exception {
		RUNNING_MODE = RunningMode.UI;
		DATA_INPUT_TYPE = DataInputType.ZIP_FILE;
		return addWorkingDayToSorushFile(sorush, pathRes, pathError, pathDuplicate, pathProccessBefore, myBin, inputType);
	}
	
	private Pair<List<String>, File> addWorkingDayToSorushFile(final File sorush, StringBuilder pathRes, StringBuilder pathError, StringBuilder pathDuplicate, StringBuilder pathProccessBefore, final Long myBin, final Integer inputType) throws Exception {
		try {
			if (!sorush.exists()) {
				throw new SorushDisagreementException(ERROR_005);
			}
			Information info = new Information(sorush.getName());
			int error = checkFilenameFormatAndGetDateOfFile(info, sorush, inputType);
			if (error < 0) {
				if (RunningMode.UI.equals(RUNNING_MODE)) {
					throw new SorushDisagreementException(ERROR_001);
				} else if (RunningMode.MAIN.equals(RUNNING_MODE)) {
					return null;
				}
			}

			List<String> workingDays = new ArrayList<String>();
			List<SorushRecord> sorushRecords = new ArrayList<SorushRecord>();
			List<SorushRecord> sorushRecords_ChangeBin = new ArrayList<SorushRecord>();

			Vector<String> all_res = new Vector<String>();
			Vector<String> all_duplicate = new Vector<String>();
			Vector<String> all_processBefore = new Vector<String>();
			Vector<String> error_records = new Vector<String>();

			sorushRecords = getSorushRecord_from_file_sorush(sorush, info, false);
			findSorushRecordsProcessing(sorushRecords);
			error_records = saveResultOfSorushFileProcessing(all_res, all_duplicate, all_processBefore, workingDays, sorushRecords, false);

			sorushRecords_ChangeBin = getSorushRecord_from_buffer_sorush(error_records, info, true);
			findSorushRecordsProcessing(sorushRecords_ChangeBin);
			error_records = saveResultOfSorushFileProcessing(all_res, all_duplicate, all_processBefore, workingDays, sorushRecords_ChangeBin, true);

			saveResult_res(sorush, pathRes, pathError, pathDuplicate,
					pathProccessBefore, all_res, all_duplicate,
					all_processBefore, error_records);
			
			File sorushRes = new File(pathRes.toString());
			if (!sorushRes.exists()) {
				logger.error(String.format("File [%s] not exist", sorushRes.getAbsoluteFile()));
			}			

			return new Pair<List<String>, File>(workingDays, sorushRes);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof SorushDisagreementException) {
				throw e;
			}
			return null;
		}
	}

	private void saveResult_res(final File sorush, StringBuilder pathRes,
			StringBuilder pathError, StringBuilder pathDuplicate,
			StringBuilder pathProccessBefore, Vector<String> all_res,
			Vector<String> all_duplicate, Vector<String> all_processBefore,
			Vector<String> error_records) throws IOException {
		//Backup result
		String sorushFileName = sorush.getName();
		String destinationPath = "/home/reports/sorsuhDisagreement_res/" + sorushFileName + "/";
		
		pathRes.delete(0, pathRes.length());
		pathError.delete(0, pathError.length());
		pathDuplicate.delete(0, pathDuplicate.length());
		pathProccessBefore.delete(0, pathProccessBefore.length());			
		
		String fileName1 = sorushFileName.substring(0, sorushFileName.length() - 4) + "-Res.txt";
		String fileName2 = sorushFileName.substring(0, sorushFileName.length() - 4) + "-Duplicate.txt";
		String fileName3 = sorushFileName.substring(0, sorushFileName.length() - 4) + "-ProccedBefore.txt";
		String fileName4 = sorushFileName.substring(0, sorushFileName.length() - 4) + "-Error.txt";
		
		pathRes.append(destinationPath + fileName1);
		pathDuplicate.append(destinationPath + fileName2);
		pathProccessBefore.append(destinationPath + fileName3);
		pathError.append(destinationPath + fileName4);
		
		saveReport(all_res, destinationPath, fileName1);
		saveReport(all_duplicate, destinationPath, fileName2);
		saveReport(all_processBefore, destinationPath, fileName3);
		saveReport(error_records, destinationPath, fileName4);
	}
	
	private Vector<String> saveResultOfSorushFileProcessing(final Vector<String> brRes, final Vector<String> brDuplicate, final Vector<String> brProccessBefore, final List<String> workingDays,
			final List<SorushRecord> sorushRecords, final Boolean changeBin)
			throws IOException {
		Vector<String> result = new Vector<String>();
		for (SorushRecord record : sorushRecords) {
			if (record.isDuplicate) {
				brDuplicate.add(record.data);
				continue;
			}
			//TASK Task103
			if (Util.hasText(record.disagrementSupplementaryDocnum)) {
				brProccessBefore.add(record.data + "|" + record.lifeCycleId + "|" + record.disagrementSupplementaryDocnum);
				continue;
			}

			if (record.workingDay != null && !workingDays.contains(record.workingDay)) {
				workingDays.add(record.workingDay);
			}
			if (record.sorushWorkingDay != null && !workingDays.contains(record.sorushWorkingDay)) {
				workingDays.add(record.sorushWorkingDay);
			}

			if (!Util.hasText(record.workingDay) || !Util.hasText(record.sorushWorkingDay)) {
				if (!SORUSH_BIN.equals(record.bankId)) { //AldComment grohe test test konad
					result.add(record.data);
				}
			} else {
				brRes.add(record.data + "|" + record.amount.toString() + "|" + record.trxType + "|" + record.lifeCycleId + "|" + record.sorushFileDate + "|" + changeBin.toString() + "|" + record.workingDay + "|" + String.valueOf(record.fee) + "|" + record.sorushPersianDt + "|" + record.sorushTrnSeqCntr + "|" + record.sorushWorkingDay + "|" + String.valueOf(record.sorushTransactionType) + "|" + record.sorushAmount.toString());
			}
		}
		return result;
	}

	private void findSorushRecordsProcessing(final List<SorushRecord> sorushRecords) {
		GeneralDao.Instance.beginTransaction();
		try {
			PersianDateFormat dateFormatPers = new PersianDateFormat("yyyyMMdd");
			Long row;
			List<Ifx> result = null;
			row = 0L;
			Ifx ifx1;
			Ifx ifx2;
			for (SorushRecord record : sorushRecords) {
				row++;
			    result = queryForSorushSettleDt(record, false);
				logger.info("after queryForSorushSettleDte, " + DateTime.now());
				logger.info("num: " + row);
				if (result.size() == 2) {
					ifx1 = result.get(0);
					ifx2 = result.get(1);
					if (!IfxType.TRANSFER_TO_ACCOUNT_RS.equals(ifx1.getIfxType()) && !IfxType.TRANSFER_TO_ACCOUNT_RS.equals(ifx2.getIfxType())) {
						record.isDuplicate = true;
					} else {
						//ifx1: sorush , ifx2: mainTrx
						if (IfxType.TRANSFER_TO_ACCOUNT_RS.equals(ifx1.getIfxType())) {
							record.sorushTrnSeqCntr = ifx1.getSrc_TrnSeqCntr();
							record.sorushPersianDt = dateFormatPers.format(ifx1.getOrigDt().getDayDate().toDate());
							record.sorushWorkingDay = dateFormatPers.format(ifx1.getSettleDt().toDate());
							record.workingDay = dateFormatPers.format(ifx2.getSettleDt().toDate());
							record.trxType = ifx2.getTrnType().toString();
							record.amount = ifx2.getAuth_Amt();
							record.lifeCycleId = String.valueOf(ifx2.getTransaction().getLifeCycleId()); //TASK Task103;
							record.disagrementSupplementaryDocnum = ifx2.getTransaction().getLifeCycle().getDisagrementSupplementaryDocnum(); //TASK Task103
							record.fee = ifx2.getTotalFeeAmt(); //Add 93.06.02
							record.sorushAmount = ifx1.getAuth_Amt(); //Add 93.05.26
							record.sorushTransactionType = ifx1.getTransaction().getTransactionType(); //Add 93.05.26
						} else {
							record.sorushTrnSeqCntr = ifx2.getSrc_TrnSeqCntr();
							record.sorushPersianDt = dateFormatPers.format(ifx2.getOrigDt().getDayDate().toDate());
							record.sorushWorkingDay = dateFormatPers.format(ifx2.getSettleDt().toDate());
							record.workingDay = dateFormatPers.format(ifx1.getSettleDt().toDate());
							record.trxType = ifx1.getTrnType().toString();
							record.amount = ifx1.getAuth_Amt();
							record.lifeCycleId = String.valueOf(ifx1.getTransaction().getLifeCycleId()); //TASK Task103;
							record.disagrementSupplementaryDocnum = ifx1.getTransaction().getLifeCycle().getDisagrementSupplementaryDocnum(); //TASK Task103
							record.fee = ifx2.getTotalFeeAmt(); //Add 93.06.02
							record.sorushAmount = ifx2.getAuth_Amt(); //Add 93.05.26
							record.sorushTransactionType = ifx2.getTransaction().getTransactionType(); //Add 93.05.26
						}
					}
				} else if (result.size() > 2) {
					record.isDuplicate = true;
				}
				ifx1 = null;
				ifx2 = null;
				result = null;
			}
			row = 0L;
			for (SorushRecord record : sorushRecords) {
				row++;
				if ((Util.hasText(record.workingDay) && Util.hasText(record.sorushWorkingDay)) || record.isDuplicate) {
					continue;
				}
			    result = queryForSorushSettleDt(record, true);
				logger.info("after queryForSorushSettleDte(Remain), " + DateTime.now());
				logger.info("num: " + row);
				if (result.size() == 2) {
					ifx1 = result.get(0);
					ifx2 = result.get(1);
					if (!IfxType.TRANSFER_TO_ACCOUNT_RS.equals(ifx1.getIfxType()) && !IfxType.TRANSFER_TO_ACCOUNT_RS.equals(ifx2.getIfxType())) {
						record.isDuplicate = true;
					} else {
						//ifx1: sorush , ifx2: mainTrx
						if (IfxType.TRANSFER_TO_ACCOUNT_RS.equals(ifx1.getIfxType())) {
							record.sorushTrnSeqCntr = ifx1.getSrc_TrnSeqCntr();
							record.sorushPersianDt = dateFormatPers.format(ifx1.getOrigDt().getDayDate().toDate());
							record.sorushWorkingDay = dateFormatPers.format(ifx1.getSettleDt().toDate());
							record.workingDay = dateFormatPers.format(ifx2.getSettleDt().toDate());
							record.trxType = ifx2.getTrnType().toString();
							record.amount = ifx2.getAuth_Amt();
							record.lifeCycleId = String.valueOf(ifx2.getTransaction().getLifeCycleId()); //TASK Task103;
							record.disagrementSupplementaryDocnum = ifx2.getTransaction().getLifeCycle().getDisagrementSupplementaryDocnum(); //TASK Task103
							record.fee = ifx2.getTotalFeeAmt(); //Add 93.06.02
							record.sorushAmount = ifx1.getAuth_Amt(); //Add 93.05.26
							record.sorushTransactionType = ifx1.getTransaction().getTransactionType(); //Add 93.05.26
						} else {
							record.sorushTrnSeqCntr = ifx2.getSrc_TrnSeqCntr();
							record.sorushPersianDt = dateFormatPers.format(ifx2.getOrigDt().getDayDate().toDate());
							record.sorushWorkingDay = dateFormatPers.format(ifx2.getSettleDt().toDate());
							record.workingDay = dateFormatPers.format(ifx1.getSettleDt().toDate());
							record.trxType = ifx1.getTrnType().toString();
							record.amount = ifx1.getAuth_Amt();
							record.lifeCycleId = String.valueOf(ifx1.getTransaction().getLifeCycleId()); //TASK Task103;
							record.disagrementSupplementaryDocnum = ifx1.getTransaction().getLifeCycle().getDisagrementSupplementaryDocnum(); //TASK Task103
							record.fee = ifx1.getTotalFeeAmt(); //Add 93.06.02
							record.sorushAmount = ifx2.getAuth_Amt(); //Add 93.05.26
							record.sorushTransactionType = ifx2.getTransaction().getTransactionType(); //Add 93.05.26
						}
					}
				} else if (result.size() > 2) {
					record.isDuplicate = true;
				} else if (result.size() == 1) {
					ifx1 = result.get(0);
					if (!IfxType.TRANSFER_TO_ACCOUNT_RS.equals(ifx1.getIfxType())) {
						record.workingDay = dateFormatPers.format(ifx1.getSettleDt().toDate());
						record.trxType = ifx1.getTrnType().toString();
						record.amount = ifx1.getAuth_Amt();
						record.lifeCycleId = String.valueOf(ifx1.getTransaction().getLifeCycleId());
						record.disagrementSupplementaryDocnum = ifx1.getTransaction().getLifeCycle().getDisagrementSupplementaryDocnum();
						record.fee = ifx1.getTotalFeeAmt(); //Add 93.06.02
					}
				}
				ifx1 = null;
				ifx2 = null;
				result = null;

			}
		} finally {
			GeneralDao.Instance.endTransaction();
		}
	}

	private List<Ifx> queryForSorushSettleDt(final SorushRecord record, final boolean needMoreSearch) {
		logger.info("before queryForSorushSettleDt, " + DateTime.now());
		long t1 = System.currentTimeMillis();
		DayDate lastDay = null;
		DayDate nextDay = null;
		if (needMoreSearch) {
			lastDay = record.trxDate.previousDay();
			nextDay = record.trxDate.nextDay();
		}
		String replaceQ_v1 = null;
		String replaceQ_v2 = null;
		String replaceQ2 = null;
		String mainQ_v1 = " i.networkTrnInfo.BankId = :bankId ";
		String mainQ_v2 = " i.ifxBankId = :bankId ";
		String mainQ2 = " ref.BankId = :bankId ";
        if ((record.bankId.toString().length() == 9) && record.bankId.toString().startsWith(SHAPARAK_ID.toString())) {
             replaceQ_v1 = String.format(" net.BankId between %1$s000 and %1$s999", SHAPARAK_ID.toString());
             replaceQ_v2 = String.format(" i.ifxBankId between %1$s000 and %1$s999", SHAPARAK_ID.toString());
             replaceQ2 = String.format(" ref.BankId between %1$s000 and %1$s999", SHAPARAK_ID.toString());
        }

        String query1_v1 = "select i"
        		 + " from Ifx i "
        		 + " inner join i.eMVRqData rq "
        		 + " inner join i.eMVRsData rs "
        		 + " inner join i.networkTrnInfo net "
                 + " where"
        		 + " i.dummycol in (0,1,2,3,4,5,6,7,8,9)"
        		 + " and i.request = false "
        		 + " and i.ifxDirection = :direction "
        		 + " and i.ifxType in (107,3,54,55,56,13,101) "
        		 + " and i.receivedDtLong between :fromDate and :toDate "
                 + " and rq.CardAcctId.AppPAN = :appPan "
                 + " and rs.RsCode = :success "
                 + " and i.trnType in (2,13,40,46,47,11,10) " //me
                 + " and i.endPointTerminalCode = :issuerTerminal "  //me
                 + " and "
                 + (replaceQ_v1 == null ? mainQ_v1 : replaceQ_v1)
                 + " and net.Src_TrnSeqCntr = :trnSeqCntr "
                 ;

        String query1_v2 = "select i"
        		+ " from Ifx i "
        		 + " where "
        		 +  " i.dummycol in (0,1,2,3,4,5,6,7,8,9)"
        		 +  " and i.request = false "
        		 +  " and i.ifxDirection = :direction "
        		 +  " and i.ifxType in (107,3,54,55,56,13,101) "
        		 +  " and i.receivedDtLong between :fromDate and :toDate "
        		 +  " and i.ifxEncAppPAN = :appPan "
        		 +  " and i.ifxRsCode = :success "
        		 +  " and i.trnType in (2,13,40,46,47,11,10) " //me
        		 +  " and i.endPointTerminalCode = :issuerTerminal "  //me
        		 +  " and "
        		 +  (replaceQ_v2 == null ? mainQ_v2 : replaceQ_v2)
        		 +  " and i.ifxSrcTrnSeqCntr = :trnSeqCntr "
        		 ;


        String query2_v1 = "select i"
        		 +  " from Ifx i "
        		 +  " inner join i.eMVRqData rq "
        		 +  " inner join i.eMVRsData rs "
        		 +  " inner join i.networkTrnInfo net "
                + " where "
                + " i.dummycol in (0,1,2,3,4,5,6,7,8,9)"
                + " and i.request = false "
                + " and i.ifxDirection = :direction "
                + " and i.ifxType = :sorushIfx  "
                + "	and rq.CardAcctId.AppPAN = :appPan "
                + " and rs.RsCode = :success "
                + " and net.BankId = :soroushBankId "
                + " and i.originalDataElements in (select ref.id from MessageReferenceData ref where ref.TrnSeqCounter = :trnSeqCntr "
                + " and ref.OrigDt.dayDate.date between :origDateFrom and :origDateTo "
                + " and ref.OrigDt.dayTime.dayTime between :origTimeFrom and :origTimeTo "
                + " and ref.AppPAN = :appPan "
                + " and " + (replaceQ2 == null ? mainQ2 : replaceQ2)
                + ")"
                ;

        String query2_v2 = "select i"
        		 +  " from Ifx i "
                + " where "
        		 +  " i.dummycol in (0,1,2,3,4,5,6,7,8,9)"
                + " and i.request = false "
                + " and i.ifxDirection = :direction "
                + " and i.ifxType = :sorushIfx  "
                + "	and i.ifxEncAppPAN = :appPan "
                + " and i.ifxRsCode = :success "
                + " and i.ifxBankId = :soroushBankId "
                + " and i.originalDataElements in (select ref.id from MessageReferenceData ref where ref.TrnSeqCounter = :trnSeqCntr "
                + " and ref.OrigDt.dayDate.date between :origDateFrom and :origDateTo "
                + " and ref.OrigDt.dayTime.dayTime between :origTimeFrom and :origTimeTo "
                + " and ref.AppPAN = :appPan "
                + " and " + (replaceQ2 == null ? mainQ2 : replaceQ2)
                + ")"
                ;


        Map<String, Object> totalParams = new HashMap<String, Object>();
        Map<String, Object> params1 = new HashMap<String, Object>();
        Map<String, Object> params2 = new HashMap<String, Object>();

        totalParams.put("direction", IfxDirection.OUTGOING);
        totalParams.put("trnSeqCntr", Integer.valueOf(record.trnSeqCntr).toString());
        totalParams.put("appPan", record.appPan);
        totalParams.put("success", ISOResponseCodes.APPROVED);


        params1.putAll(totalParams);
        params2.putAll(totalParams);

        DateTime from = new DateTime(record.trxDate, new DayTime(0, 0, 0));
        DateTime to = new DateTime(record.trxDate, new DayTime(23, 59, 59));
        if (needMoreSearch) {
        	from = new DateTime(lastDay, new DayTime(0, 0, 0));
        	to = new DateTime(nextDay, new DayTime(23, 59, 59));
        }
        params1.put("fromDate", from.getDateTimeLong());
        params1.put("toDate", to.getDateTimeLong());
        params1.put("issuerTerminal", ISSUER_SHETAB_TERMINAL_ID);


//old        params2.put("sorushIfx", IfxType.SORUSH_REV_REPEAT_RS);
        params2.put("sorushIfx", IfxType.TRANSFER_TO_ACCOUNT_RS);
        params2.put("soroushBankId", 936450L);
        params2.put("origDateFrom", from.getDayDate().getDate());
        params2.put("origDateTo", to.getDayDate().getDate());
        params2.put("origTimeFrom", from.getDayTime().getDayTime());
        params2.put("origTimeTo", to.getDayTime().getDayTime());
        if (replaceQ_v1 == null || replaceQ_v2 == null) {
			params1.put("bankId", record.bankId);
		}

        if (replaceQ2 == null) {
			params2.put("bankId", record.bankId);
		}
        /********************************************************************/

        logger.info("time 1 : " + String.valueOf((System.currentTimeMillis() - t1)));
        t1 = System.currentTimeMillis();
		List<Ifx> result = GeneralDao.Instance.find(query1_v2, params1);
        logger.info("query 1 v2 : " + String.valueOf((System.currentTimeMillis() - t1)));
        if (result == null || result.size() <= 0) {
	        t1 = System.currentTimeMillis();
			result = GeneralDao.Instance.find(query1_v1, params1);
	        logger.info("query 1 v1 : " + String.valueOf((System.currentTimeMillis() - t1)));
        }

        t1 = System.currentTimeMillis();
        List<Ifx> result2 = GeneralDao.Instance.find(query2_v2, params2);
        logger.info("query 2 v2: " + String.valueOf((System.currentTimeMillis() - t1)));
        if (result2 == null || result2.size() <= 0) {
            t1 = System.currentTimeMillis();
            result2 = GeneralDao.Instance.find(query2_v1, params2);
            logger.info("query 2 v2: " + String.valueOf((System.currentTimeMillis() - t1)));
        }

        result.addAll(result2);

        return result;
	}

	@Deprecated
	private List<Ifx> queryForSorushSettleDt_v1(final SorushRecord record, final boolean needMoreSearch) {
		logger.info("before queryForSorushSettleDt, " + DateTime.now());
		long t1 = System.currentTimeMillis();
		DayDate lastDay = null;
		DayDate nextDay = null;
		if (needMoreSearch) {
			lastDay = record.trxDate.previousDay();
			nextDay = record.trxDate.nextDay();
		}
		String replaceQ = null;
		String replaceQ2 = null;
		String mainQ = " i.networkTrnInfo.BankId = :bankId ";
		String mainQ2 = " ref.BankId = :bankId ";
        if ((record.bankId.toString().length() == 9) && record.bankId.toString().startsWith(SHAPARAK_ID.toString())) {
             replaceQ = String.format(" net.BankId between %1$s000 and %1$s999", SHAPARAK_ID.toString());
             replaceQ2 = String.format(" ref.BankId between %1$s000 and %1$s999", SHAPARAK_ID.toString());
        }

        String query1 = "select i"
        		 +  " from Ifx i "
        		 +  " inner join i.eMVRqData rq "
        		 +  " inner join i.eMVRsData rs "
        		 +  " inner join i.networkTrnInfo net "
                + " where"
        		 +  " i.dummycol in (0,1,2,3,4,5,6,7,8,9)"
        		 +  " and i.request = false "
        		 +  " and i.ifxDirection = :direction "
        		 +  " and i.ifxType in (107,3,54,55,56,58,13,101) "
        		 +  " and i.receivedDtLong between :fromDate and :toDate "
                + "	and rq.CardAcctId.AppPAN = :appPan "
                + " and rs.RsCode = :success "
                + " and i.trnType in (2,13,40,46,47,11,10) " //me
                + " and i.endPointTerminalCode = :issuerTerminal "  //me
                + " and "
                + (replaceQ == null ? mainQ : replaceQ)
                + " and net.Src_TrnSeqCntr = :trnSeqCntr "
                ;


        String query2 = "select i"
        		 +  " from Ifx i "
        		 +  " inner join i.eMVRqData rq "
        		 +  " inner join i.eMVRsData rs "
        		 +  " inner join i.networkTrnInfo net "
                + " where "
        		 +  " i.dummycol in (0,1,2,3,4,5,6,7,8,9)"
                + " and i.request = false "
                + " and i.ifxDirection = :direction "
                + " and i.ifxType = :sorushIfx  "
                + "	and rq.CardAcctId.AppPAN = :appPan "
                + " and rs.RsCode = :success "
                + " and net.BankId = :soroushBankId "
                + " and i.originalDataElements in (select ref.id from MessageReferenceData ref where ref.TrnSeqCounter = :trnSeqCntr "
                + " and ref.OrigDt.dayDate.date between :origDateFrom and :origDateTo "
                + " and ref.OrigDt.dayTime.dayTime between :origTimeFrom and :origTimeTo "
                + " and ref.AppPAN = :appPan "
                + " and " + (replaceQ2 == null ? mainQ2 : replaceQ2)
                + ")"
                ;
        Map<String, Object> totalParams = new HashMap<String, Object>();
        Map<String, Object> params1 = new HashMap<String, Object>();
        Map<String, Object> params2 = new HashMap<String, Object>();

        totalParams.put("direction", IfxDirection.OUTGOING);
        totalParams.put("trnSeqCntr", Integer.valueOf(record.trnSeqCntr).toString());
        totalParams.put("appPan", record.appPan);
        totalParams.put("success", ISOResponseCodes.APPROVED);


        params1.putAll(totalParams);
        params2.putAll(totalParams);

        DateTime from = new DateTime(record.trxDate, new DayTime(0, 0, 0));
        DateTime to = new DateTime(record.trxDate, new DayTime(23, 59, 59));
        if (needMoreSearch) {
        	from = new DateTime(lastDay, new DayTime(0, 0, 0));
        	to = new DateTime(nextDay, new DayTime(23, 59, 59));
        }
        params1.put("fromDate", from.getDateTimeLong());
        params1.put("toDate", to.getDateTimeLong());
        params1.put("issuerTerminal", ISSUER_SHETAB_TERMINAL_ID);


//old        params2.put("sorushIfx", IfxType.SORUSH_REV_REPEAT_RS);
        params2.put("sorushIfx", IfxType.TRANSFER_TO_ACCOUNT_RS);
        params2.put("soroushBankId", 936450L);
        params2.put("origDateFrom", from.getDayDate().getDate());
        params2.put("origDateTo", to.getDayDate().getDate());
        params2.put("origTimeFrom", from.getDayTime().getDayTime());
        params2.put("origTimeTo", to.getDayTime().getDayTime());
        if (replaceQ == null) {
			params1.put("bankId", record.bankId);
		}

        if (replaceQ2 == null) {
			params2.put("bankId", record.bankId);
		}
        /********************************************************************/

//        String query = "select i from Ifx i "
//        		 +  " where "
//        		 +  " i.dummycol in (0,1,2,3,4,5,6,7,8,9)"
//                + "	and i.eMVRqData.CardAcctId.AppPAN = :appPan "
//                + " and i.eMVRsData.RsCode = :success"
//                + " and i.request = false "
//                + " and i.ifxDirection = :direction"
//                + " and (("
//                + (replaceQ == null ? mainQ : replaceQ)
//                + " and i.ifxType not in " + IfxType.strRevRsOrdinals  + " and i.networkTrnInfo.Src_TrnSeqCntr = :trnSeqCntr and i.receivedDtLong between :fromDate and :toDate)" ;
//        Map<String, Object> params = new HashMap<String, Object>();
//        DateTime from = new DateTime(record.trxDate, new DayTime(0, 0, 0));
//        DateTime to = new DateTime(record.trxDate, new DayTime(23, 59, 59));
//        if (needMoreSearch) {
//        	from = new DateTime(lastDay, new DayTime(0, 0, 0));
//        	to = new DateTime(nextDay, new DayTime(23, 59, 59));
//        }
//        params.put("fromDate",from.getDateTimeLong());
//        params.put("toDate", to.getDateTimeLong());
//        params.put("trnSeqCntr", Integer.valueOf(record.trnSeqCntr).toString());
//        params.put("appPan", record.appPan);
//        params.put("success", ErrorCodes.APPROVED);
//        params.put("direction", IfxDirection.OUTGOING);
//        if (replaceQ == null)
//            params.put("bankId", record.bankId);
//        List<Ifx> ifxs = GeneralDao.Instance.find(query, params);
//        if (ifxs.size() == 1)
//        {
//        	Ifx ifx = ifxs.get(0);
//        	query = "select i"
//            	 +  " from Ifx i "
//                + " where "
//                + " i.dummycol in (0,1,2,3,4,5,6,7,8,9)"
//                + "	and i.eMVRqData.CardAcctId.AppPAN = :appPan "
//                + " and i.eMVRsData.RsCode = :success"
//                + " and i.request = false "
//                + " and i.ifxDirection = :direction"
//                + " and i.ifxType = :sorushIfx  " 
//                + " and i.networkTrnInfo.BankId = :soroushBankId "
//                + " and i.originalDataElements in (select ref.id from MessageReferenceData ref where ref.TrnSeqCounter = :trnSeqCntr "
//                + " and ref.OrigDt.dayDate.date :origDate"
//                + " and ref.OrigDt.dayTime.dayTime :origTime"
//                + " and ref.AppPAN = :appPan"
//                + " and " + (replaceQ2 == null ? mainQ2 : replaceQ2);
//        	params.put("sorushIfx", IfxType.SORUSH_REV_REPEAT_RS);
//            params.put("soroushBankId", 936450L);
//            params.put("origDate", ifx.getOrigDt().getDayDate());
//            params.put("origTime", ifx.getOrigDt().getDayTime());
//            if (replaceQ2 == null)
//                params.put("bankId", record.bankId);
//        }

        logger.info("time 1 : " + String.valueOf((System.currentTimeMillis() - t1)));
        t1 = System.currentTimeMillis();
		List<Ifx> result = GeneralDao.Instance.find(query1, params1);
        logger.info("query 1 : " + String.valueOf((System.currentTimeMillis() - t1)));
        t1 = System.currentTimeMillis();
        result.addAll(GeneralDao.Instance.find(query2, params2));
        logger.info("query 2 : " + String.valueOf((System.currentTimeMillis() - t1)));

        return result;
	}


	public final List<String> findDisagreementFromUI(final File sorush, final String zipPath, final String resultPath, final Integer inputType) throws Exception {
		RUNNING_MODE = RunningMode.UI;
		DATA_INPUT_TYPE = DataInputType.ZIP_FILE;
		return findDisagreement(sorush, sorush.getName(), zipPath, resultPath, inputType, false, null, null, null, null, "first run");
	}

	private List<String> findDisagreement(final File sorush, final String zipPath, final String resultPath, final Integer inputType) throws Exception {
		return findDisagreement(sorush, sorush.getName(), zipPath, resultPath, inputType, false, null, null, null, null, "first run");
	}

	@SuppressWarnings("unchecked")
	private List<String> findDisagreement(final Object sorush, final String postResutlName, final String zipPath, String resultPath, final Integer inputType, final Boolean skipCheckDateInCompare, Vector<String> v_all_sanad_result, Vector<String> v_all_other_result, Vector<String> v_all_error_tt_result, Vector<String> v_all_sanad_error_result, final String runDesc) throws Exception {
		try {
			logger.info(String.format("Running findDisagrement for [%s] [%s]", postResutlName, runDesc != null ? runDesc : ""));
			logger.info(String.format("Running findDisagrement for [%s] [%s]", postResutlName, runDesc != null ? runDesc : ""));
			Information info = new Information(postResutlName);

			/******************* initialize path of results ***********************/
			File folders_bank = null;
			File folders_shetab = null;
			if (DataInputType.ZIP_FILE.equals(DATA_INPUT_TYPE)) {
				if (!zipPath.endsWith(".zip")) {
					throw new SorushDisagreementException(ERROR_002);
				}
				folders_bank = unZip(zipPath, zipPath.substring(0, zipPath.indexOf(".zip")));
				folders_shetab = folders_bank;
			} else if (DataInputType.FOLDER.equals(DATA_INPUT_TYPE)) {
				folders_bank = new File(zipPath);
				folders_shetab = new File(zipPath);
			}
			if (folders_bank == null || !folders_bank.exists()) {
				logger.error(String.format("data folder bank '%s' not exist !!!", folders_bank != null ? folders_bank.getAbsolutePath() : ""));
			}
			
			if (folders_shetab == null || !folders_shetab.exists()) {
				logger.error(String.format("data folder shetab '%s' not exist !!!", folders_bank != null ? folders_bank.getAbsolutePath() : ""));
			}
			

			if (!resultPath.endsWith("/")) {
				resultPath  += "/";
			}


			/************************************ Define value *******************************/
			//Result files
			Vector<String> v_sanad_result = new Vector<String>();
			if (v_all_sanad_result == null) {
				v_all_sanad_result = new Vector<String>();
			}

			Vector<String> v_sanad_error_result = new Vector<String>();
			if (v_all_sanad_error_result == null) {
				v_all_sanad_error_result = new Vector<String>();
			}

			Vector<String> v_error_result = new Vector<String>();

			Vector<String> v_other_result = new Vector<String>();
			if (v_all_other_result == null) {
				v_all_other_result = new Vector<String>();
			}

			Vector<String> v_error_tt_result = new Vector<String>();
			if (v_all_error_tt_result == null) {
				v_all_error_tt_result = new Vector<String>();
			}


			List<SorushRecord> sorushRecords = null; //
			List<String> workingDays = new ArrayList<String>();
			List<String> allWorkingDays = new ArrayList<String>(); //for sorush working days
			List<String> sorushWorkingDays = new ArrayList<String>();
			Map<String, BufferedReader> map = new HashMap<String, BufferedReader>();
			long t1 = System.currentTimeMillis();
			/******************************* Extract sorush with working days******************/
			if (sorush instanceof File) {
				sorushRecords = extractSorushRecords(info, (File)sorush, v_error_tt_result, sorushRecords, inputType);
			} else if (sorush instanceof Vector<?>) {
				sorushRecords = extractSorushRecords(info, ((Vector<String>)sorush), v_error_tt_result, sorushRecords, inputType, skipCheckDateInCompare);
			} else {
				//Error
			}
			if (sorushRecords == null) {
				logger.error(String.format("error in extract soroushRecord from %s", postResutlName));
				return null;
			}
			logger.info(String.format("finish extract soroushRecord from %s", postResutlName));

			/******************************* Fill List of AllWorkingDays & WorkingDays *******/
			fillWorkingsDays(sorushRecords, allWorkingDays, workingDays, sorushWorkingDays);
			/******************************* Check Data Need is Complete *********************/
			checkWorkingDaysIsComplete(folders_bank, folders_shetab, allWorkingDays);
			/******************************* Map Working Days to BufferReader ****************/	
			readZipContainsForm8BankForWorkingDays(folders_bank, workingDays, map);
			readZipContainsForm8ShetabForWorkingDays(folders_shetab, workingDays, map);
			/****************** read forms processing ****************************************/
			t1 = System.currentTimeMillis();
			readFormsProcessing(sorushRecords, workingDays, map, skipCheckDateInCompare);
//			//************** Only for test
//			for (SorushRecord soroshRecord : sorushRecords) {
//				logger.info(soroshRecord.appPan + " - " + soroshRecord.in8Bank + " - " + soroshRecord.in8Shetab);
//			}
			//**************
			logger.info("**** Read Forms Time : " + (System.currentTimeMillis() - t1));
			logger.info("**** Read Forms Time : " + (System.currentTimeMillis() - t1));
			/******************** search for sorush transfer trx ***************/
			readZipContasinForm8BankForSorushWorkingDays(folders_bank, map, sorushWorkingDays);
			readZipContasinForm8ShetabForSorushWorkingDays(folders_shetab, map, sorushWorkingDays);
			/****************** read sorush forms processing *******************/
			t1 = System.currentTimeMillis();
			readSorushFormsProcessing(sorushRecords, map, sorushWorkingDays, skipCheckDateInCompare);
//			//************** Only for test
//			for (SorushRecord soroshRecord : sorushRecords) {
//				logger.info(soroshRecord.appPan + " - " + soroshRecord.soroushIn8Bank + " - " + soroshRecord.sorushIn8Shetab);
//			}
			/*****************************  ********************************/
			logger.info("****  Read Forms Sorush Time  : " + (System.currentTimeMillis() - t1));
			logger.info("****  Read Forms Sorush Time  : " + (System.currentTimeMillis() - t1));
			/************************ Sanad ********************************/
			sanadProcessing(info, v_sanad_result, v_error_result, v_other_result, v_sanad_error_result, sorushRecords, skipCheckDateInCompare);
			//******
			/***************************** Save Result **************************************/
			saveResults(v_sanad_result,
					v_other_result,
					v_error_tt_result, v_sanad_error_result, v_all_sanad_result, v_all_other_result, v_all_error_tt_result, v_all_sanad_error_result);

			//Yekbar Digar Run Shavad
			if (v_error_result.size() > 0 && InputType.SORUSH_RES.equals(inputType)) {
				findDisagreement(v_error_result, postResutlName, zipPath, resultPath, InputType.SORUSH_RES_AFTER_SANAD, true, v_all_sanad_result, v_all_other_result, v_all_error_tt_result, v_all_sanad_error_result, "skip check date in compare");
			} else {
				//save final result
				BufferedWriter final_sanad_result = new BufferedWriter(new FileWriter(new File(resultPath + "final_sanad_result_" + postResutlName.replace("error_result_", ""))));
				BufferedWriter final_error_result = new BufferedWriter(new FileWriter(new File(resultPath + "final_error_result_" + postResutlName.replace("error_result_", ""))));
				BufferedWriter final_other_result = new BufferedWriter(new FileWriter(new File(resultPath + "final_other_result_" + postResutlName.replace("error_result_", ""))));
				BufferedWriter final_error_tt_result = new BufferedWriter(new FileWriter(new File(resultPath + "final_error_tt_result_" + postResutlName.replace("error_result_", ""))));
				BufferedWriter final_sanad_error_result = new BufferedWriter(new FileWriter(new File(resultPath + "final_sanad_with_error_result_" + postResutlName.replace("error_result_", ""))));
				saveFinalResults(final_sanad_result, v_all_sanad_result, final_error_result,
						v_error_result, final_other_result, v_all_other_result,
						final_error_tt_result, v_all_error_tt_result, final_sanad_error_result, v_all_sanad_error_result);


				backupResult_sanad(postResutlName, v_all_sanad_result,
						v_all_other_result, v_all_error_tt_result,
						v_all_sanad_error_result, v_error_result);


				//TASK Task103
				saveDocumentNumberToDatabase(v_all_sanad_result);

			}

		} catch (Exception e) {
			logger.error(String.format("Error in findDisagreement in file %s - %s ", postResutlName, e));
            throw e;
			//e.printStackTrace();
		}

		List<String> res = new ArrayList<String>();
		res.add(resultPath + "final_sanad_result_" + postResutlName.replace("error_result_", ""));
		res.add(resultPath + "final_error_result_" + postResutlName.replace("error_result_", ""));
		res.add(resultPath + "final_other_result_" + postResutlName.replace("error_result_", ""));
		res.add(resultPath + "final_error_tt_result_" + postResutlName.replace("error_result_", ""));
		res.add(resultPath + "final_sanad_with_error_result_" + postResutlName.replace("error_result_", ""));
		return res;
	}

	private void backupResult_sanad(final String postResultName,
			final Vector<String> v_all_sanad_result,
			final Vector<String> v_all_other_result,
			final Vector<String> v_all_error_tt_result,
			final Vector<String> v_all_sanad_error_result,
			final Vector<String> v_error_result) throws IOException {
		//Backup result
		String destinationPath = "/home/reports/sorsuhDisagreement/" + postResultName + "/";
		String fileName1 = "final_sanad_result_" + postResultName;
		String fileName2 = "final_error_result_" + postResultName;
		String fileName3 = "final_other_result_" + postResultName;
		String fileName4 = "final_error_tt_result_" + postResultName;
		String fileName5 = "final_sanad_with_error_result_" + postResultName;
		saveReport(v_all_sanad_result, destinationPath, fileName1);
		saveReport(v_error_result, destinationPath, fileName2);
		saveReport(v_all_other_result, destinationPath, fileName3);
		saveReport(v_all_error_tt_result, destinationPath, fileName4);
		saveReport(v_all_sanad_error_result, destinationPath, fileName5);
	}

	//TASK Task103
	private void saveDocumentNumberToDatabase(final Vector<String> v_all_sanad_result) {
		GeneralDao.Instance.beginTransaction();

		PersianDateFormat dateFormatPers = new PersianDateFormat("yyyyMMdd");
		int row = 0;
		SorushRecord record = null;
		StringTokenizer tokenizer = null;
		try {
			for (String line : v_all_sanad_result) {
				row++;
				logger.info("sorush" + row + ": " + line);
				record = new SorushRecord();
				tokenizer = new StringTokenizer(line, "|");
				record.persianDt =  tokenizer.nextToken().trim();
				Date date = dateFormatPers.parse(record.persianDt);
				record.trxDate = new DayDate(date);
				record.trnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, tokenizer.nextToken().trim(), '0');
				if (record.trnSeqCntr != null && !record.trnSeqCntr.equals("null")) {
					record.trnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, record.trnSeqCntr, '0');
				}
				record.appPan = tokenizer.nextToken().trim();
				record.bankId = Long.valueOf(tokenizer.nextToken().trim());

				if (tokenizer.hasMoreTokens()) {
					record.workingDay = tokenizer.nextToken().trim();
					record.sorushPersianDt = tokenizer.nextToken().trim();
					record.sorushTrnSeqCntr = tokenizer.nextToken().trim();
					if (record.sorushTrnSeqCntr != null && !record.sorushTrnSeqCntr.equals("null")) {
						record.sorushTrnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, record.sorushTrnSeqCntr, '0');
					}
					record.sorushWorkingDay = tokenizer.nextToken().trim();
					record.trxType = tokenizer.nextToken().trim();
					record.amount = Long.valueOf(tokenizer.nextToken().trim());

					String nullString = "null";
					//Niaz hast check shavad
					if (record.sorushPersianDt.toLowerCase().equals(nullString)) {
						record.sorushPersianDt = null;
					}

					if (record.sorushTrnSeqCntr.toLowerCase().equals(nullString)) {
						record.sorushTrnSeqCntr = null;
					}

					if (record.sorushWorkingDay.toLowerCase().equals(nullString)) {
						record.sorushWorkingDay = null;
					}

					record.lifeCycleId = tokenizer.nextToken().trim();
					record.sorushFileDate = tokenizer.nextToken().trim();
					tokenizer.nextToken(); //skipCheckDateInCompare
					record.isChangeBin = Boolean.valueOf(tokenizer.nextToken().trim());
					record.documentNumber = tokenizer.nextToken().trim();
					record.documentPattern = tokenizer.nextToken().trim();
				}

				if (Util.hasText(record.lifeCycleId) && Util.hasText(record.documentNumber) && !"null".equals(record.lifeCycleId.toLowerCase()) && !"null".equals(record.documentNumber.toLowerCase())) {
					logger.info(String.format("save record lifeCycleId : %s, sanadTransId : %s , record [%s]", record.lifeCycleId, record.documentNumber, line));
					LifeCycle lifeCycle = GeneralDao.Instance.load(LifeCycle.class, Long.valueOf(record.lifeCycleId));
					lifeCycle.setDisagrementSupplementaryDocnum(record.documentNumber);
					GeneralDao.Instance.saveOrUpdate(lifeCycle);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			logger.error(String.format("Error occured in saving into lifeCycle Tabel - appPan : %s ,trnSeqCntr : %s, docNum : %s lifeCycle : %s ", record.appPan, record.trnSeqCntr, record.documentNumber, record.lifeCycleId));
			//e.printStackTrace();
		} finally {
			GeneralDao.Instance.endTransaction();
		}

	}

	private int checkFilenameFormatAndGetDateOfFile(final Information info, final File sorush, final Integer inputType) {
		if (InputType.SORUSH_RES.equals(inputType)) {
			String dateFile = sorush.getName().substring(22, 28);   //File Name Sample : "test sorosh - test100-920225-1-res.txt"  //AldTODO intekke code mitavanad hazf shavad
				if (dateFile.startsWith("9")) {
					info.setDate(dateFile);
				} else {
					dateFile = sorush.getName().substring(_SORUSH_PRE_FILENAME.length(), _SORUSH_PRE_FILENAME.length() + 6); //sample : isspas921101-1092-53214-res.txt || issRST920427-1092-21190
					if (dateFile.startsWith("9")) {
						info.setDate(dateFile);
					} else {
						logger.error(String.format("File '%s' name is incorrect !!!", sorush.getName()));
						return -1;
					}
				}
		} else if (InputType.SORUSH.equals(inputType)) {
			if (!sorush.getName().toLowerCase().startsWith(_SORUSH_PRE_FILENAME.toLowerCase())) {
				return -1;
			}
			String dateFile = sorush.getName().substring(_SORUSH_PRE_FILENAME.length(), _SORUSH_PRE_FILENAME.length() + 6); //sample : isspas921101-1092-53214.txt
			if (dateFile.startsWith("9")) {
				info.setDate(dateFile);
			} else {
				logger.error(String.format("File '%s' name is incorrect !!!", sorush.getName()));  //File Name Sampe : "test sorosh - test100-920225-1-res.txt"
				return -1;
			}
		}
		return 1;
	}

	private void readSorushFormsProcessing(final List<SorushRecord> sorushRecords, final Map<String, BufferedReader> map, final List<String> sorushWorkingDays, final Boolean skipCheckDateInCompare) throws InterruptedException {
		int subListSize;
		Thread[] readSoroushFilesThreads = null;

		Integer threadCountSorush = READ_FORMS_THREAD_COUNT_SORUSH;
		if (sorushWorkingDays.size() > READ_FORMS_THREAD_COUNT_SORUSH) {
			readSoroushFilesThreads = new Thread[threadCountSorush];
		} else {
			threadCountSorush = sorushWorkingDays.size() > MAXIMUM_THREAD_FOR_READ_FILE ? MAXIMUM_THREAD_FOR_READ_FILE : sorushWorkingDays.size();
			readSoroushFilesThreads = new Thread[threadCountSorush];
		}
		subListSize = (int) Math.ceil(sorushWorkingDays.size() / (double) threadCountSorush);
		for (int i = 0; i < readSoroushFilesThreads.length; i++) {
			ReadFormBankShatabSorushThread issueFCBThread = new ReadFormBankShatabSorushThread(this, (i + 1), sorushWorkingDays.subList(i * subListSize, Math.min((i + 1) * subListSize, sorushWorkingDays.size())), map, (Vector<SorushRecord>) sorushRecords, skipCheckDateInCompare);
			Thread readSoroushFileThread = new Thread(issueFCBThread);
			readSoroushFilesThreads[i] = readSoroushFileThread;
			logger.debug("Thread: " + readSoroushFileThread.getName() + " is starting...");
			readSoroushFileThread.start();
		}

		for (int i = 0; i < readSoroushFilesThreads.length; i++) {
			if (readSoroushFilesThreads != null) {
				readSoroushFilesThreads[i].join();
				logger.debug("readFilesThread: " + i + "[" + readSoroushFilesThreads[i].getName() + "] joined");
			}
		}
	}

	private void readFormsProcessing(final List<SorushRecord> sorushRecords, final List<String> workingDays, final Map<String, BufferedReader> map, final Boolean skipCheckDateInCompare)
			throws InterruptedException {
		Thread[] readFilesThreads = null;

		Integer threadCount = READ_FORMS_THREAD_COUNT;
		if (workingDays.size() > READ_FORMS_THREAD_COUNT) {
			readFilesThreads = new Thread[threadCount];
		} else {
			threadCount = workingDays.size() > MAXIMUM_THREAD_FOR_READ_FILE ? MAXIMUM_THREAD_FOR_READ_FILE : workingDays.size();
			readFilesThreads = new Thread[threadCount];
		}
		int subListSize = (int) Math.ceil(workingDays.size() / (double) threadCount);
		for (int i = 0; i < readFilesThreads.length; i++) {
			ReadFormBankShatabThread issueFCBThread = new ReadFormBankShatabThread(this, (i + 1), workingDays.subList(i * subListSize, Math.min(( i + 1) * subListSize, workingDays.size())), map, (Vector<SorushRecord>) sorushRecords, skipCheckDateInCompare);
			Thread readFileThread = new Thread(issueFCBThread);
			readFilesThreads[i] = readFileThread;
			logger.debug("Thread: " + readFileThread.getName() + " is starting...");
			readFileThread.start();
		}

		for (int i = 0; i < readFilesThreads.length; i++) {
			if (readFilesThreads != null) {
				readFilesThreads[i].join();
				logger.debug("readFilesThread: " + i + "[" + readFilesThreads[i].getName() + "] joined");
			}
		}
	}

	private void sanadProcessing(final Information info,
			final Vector<String> v_sanad_result, final Vector<String> v_error_result,
			final Vector<String> v_other_result, final Vector<String> v_sanad_error_result, final List<SorushRecord> sorushRecords, final Boolean skipCheckDateInCompare)
			throws InterruptedException, IOException {
		int subListSize;
		Thread[] threads = null;
		if (sorushRecords.size() > SANAD_THREAD_COUNT) {
			threads = new Thread[SANAD_THREAD_COUNT];
			subListSize = (int) Math.ceil(sorushRecords.size() / (double) SANAD_THREAD_COUNT);
			for (int i = 0; i < threads.length; i++) {
				info.setThreadId(i + 1);
				SanadThread issueFCBThread = null;
				if ((i * subListSize) < sorushRecords.size()) {
					issueFCBThread = new SanadThread(this, info, skipCheckDateInCompare, sorushRecords.subList(i * subListSize, Math.min((i + 1) * subListSize, sorushRecords.size())), v_sanad_result, v_error_result, v_other_result, v_sanad_error_result);
				} else {
					issueFCBThread = new SanadThread(this, info, skipCheckDateInCompare, new ArrayList<SorushRecord>(), v_sanad_result, v_error_result, v_other_result, v_sanad_error_result);  //empty list
				}
				Thread issueThread = new Thread(issueFCBThread);
				threads[i] = issueThread;
				logger.debug("Thread: " + issueThread.getName() + " is starting...");
				issueThread.start();
			}

			for (int i = 0; i < threads.length; i++) {
				if (threads != null) {
					threads[i].join();
					logger.debug("issueThread: " + i + "[" + threads[i].getName() + "] joined");
				}
			}

		} else {
			/************************* Write middle result to files ****************************/
			info.setThreadId(1);
			processSoroushRecords(info, skipCheckDateInCompare, v_sanad_result, v_error_result, v_other_result, v_sanad_error_result, sorushRecords);
		}
	}

	private void saveFinalResults(final BufferedWriter sanad_result,
			final Vector<String> v_all_sanad_result, final BufferedWriter error_result,
			final Vector<String> v_all_error_result, final BufferedWriter other_result,
			final Vector<String> v_all_other_result, final BufferedWriter error_tt_result,
			final Vector<String> v_all_error_tt_result, final BufferedWriter sanad_error_result,
			final Vector<String> v_all_sanad_error_result) throws IOException {
		//result
		for (String record : v_all_sanad_result) {
			sanad_result.append(record).append("\r\n");
		}

		//error result
		for (String record : v_all_error_result) {
			error_result.append(record).append("\r\n");
		}

		//other result
		for (String record : v_all_other_result) {
			other_result.append(record).append("\r\n");
		}

		//error_tt result
		for (String record : v_all_error_tt_result) {
			error_tt_result.append(record).append("\r\n");
		}

		//result with error
		for (String record : v_all_sanad_error_result) {
			sanad_error_result.append(record).append("\r\n");
		}

		//******* Close Files
		sanad_result.close();
		//sanad_result.flush();
		error_result.close();
		//error_result.flush();

		other_result.close();
		//other_result.flush();

		error_tt_result.close();
		//error_tt_result.flush();
		sanad_error_result.close();
		//sanad_error_result.flush();
	}

	private void saveResults(
			final Vector<String> v_sanad_result,
			final Vector<String> v_other_result,
			final Vector<String> v_error_tt_result, final Vector<String> v_sanad_error_result, final Vector<String> v_all_sanad_result, final Vector<String> v_all_other_result, final Vector<String> v_all_error_tt_result, final Vector<String> v_all_sanad_error_result) throws IOException {
		//result
		for (String record : v_sanad_result) {
			v_all_sanad_result.add(record);
		}

		//other result
		for (String record : v_other_result) {
			v_all_other_result.add(record);
		}

		//error_tt result
		for (String record : v_error_tt_result) {
			v_all_error_tt_result.add(record);
		}

		//result with error
		for (String record : v_sanad_error_result) {
			v_all_sanad_error_result.add(record);
		}

	}

	private void readZipContasinForm8BankForSorushWorkingDays(
			final File folders, final Map<String, BufferedReader> map,
			final List<String> sorushWorkingDays) throws IOException {
		for (String sorushWorkingDay : sorushWorkingDays) {
				for (final File folder : folders.listFiles()) {
					if (sorushWorkingDay.equals(folder.getName())) {
						readZipContasinForm8Bank(folder, map, sorushWorkingDay.substring(2));
						break;
					}
				}
		}
	}
	
	private void readZipContasinForm8ShetabForSorushWorkingDays(
			final File folders, final Map<String, BufferedReader> map,
			final List<String> sorushWorkingDays) throws IOException {
		for (String sorushWorkingDay : sorushWorkingDays) {
				for (final File folder : folders.listFiles()) {
					if (sorushWorkingDay.equals(folder.getName())) {
						readZipContasinForm8Shetab(folder, map, sorushWorkingDay.substring(2));
						break;
					}
				}
		}
	}	

	private void readZipContainsForm8BankForWorkingDays(final File folders,
			final List<String> workingDays, final Map<String, BufferedReader> map)
			throws IOException {
		for (final File folder : folders.listFiles()) {
			for (String workingDay :workingDays) {
				if (workingDay.equals(folder.getName())) {
					readZipContasinForm8Bank(folder, map, workingDay.substring(2));
					break;
				}
			}
		}
	}

	private void readZipContainsForm8ShetabForWorkingDays(final File folders,
			final List<String> workingDays, final Map<String, BufferedReader> map)
			throws IOException {
		for (final File folder : folders.listFiles()) {
			for (String workingDay :workingDays) {
				if (workingDay.equals(folder.getName())) {
					readZipContasinForm8Shetab(folder, map, workingDay.substring(2));
					break;
				}
			}
		}
	}

	private void checkWorkingDaysIsComplete(final File folders_bank, final File folders_shetab, final List<String> allWorkingDays) throws SorushDisagreementException {
		/*********************** check workingDays from UI is complete ********************************/
		//bank
		List<String> folderNameWorkingDays_bank = new ArrayList<String>();
		for (final File folder : folders_bank.listFiles()) {
			folderNameWorkingDays_bank.add(folder.getName());
		}
		//shetab
		List<String> folderNameWorkingDays_shetab = new ArrayList<String>();
		for (final File folder : folders_shetab.listFiles()) {
			folderNameWorkingDays_shetab.add(folder.getName());
		}

		List<String> missingWorkingDays_bank = new ArrayList<String>();
		List<String> problemWorkingDays_bank = new ArrayList<String>();
		List<String> missingWorkingDays_shetab = new ArrayList<String>();
		List<String> problemWorkingDays_shetab = new ArrayList<String>();
		missingWorkingDays_bank.clear();
		problemWorkingDays_bank.clear();
		missingWorkingDays_shetab.clear();
		problemWorkingDays_shetab.clear();
		for (String workingDay1 : allWorkingDays) {
			if (!folderNameWorkingDays_bank.contains(workingDay1)) {
				logger.info(String.format("folder %s is not exist", workingDay1));
				missingWorkingDays_bank.add(workingDay1);
			} else { //Check file of folder
				File fileBank = new File(folders_bank.getAbsoluteFile() + "/" + workingDay1 + "/" + BANK_NAME + workingDay1.substring(2) + ".zip");
				if (!fileBank.exists()) {
					problemWorkingDays_bank.add(workingDay1);
				}
			}
			
			if (!folderNameWorkingDays_shetab.contains(workingDay1)) {
				logger.info(String.format("folder %s is not exist", workingDay1));
				missingWorkingDays_shetab.add(workingDay1);
			} else { //Check file of folder
				File fileShetab = new File(folders_shetab.getAbsoluteFile() + "/" + workingDay1 + "/" + SHETAB_NAME + workingDay1.substring(2) + ".zip");
				if (!fileShetab.exists()) {
					problemWorkingDays_shetab.add(workingDay1);
				}
			}			
		}
		
		if (!missingWorkingDays_bank.isEmpty()) {
			throw new SorushDisagreementException(String.format(ERROR_003, missingWorkingDays_bank.toString()));
		}
		if (!problemWorkingDays_bank.isEmpty()) {
			throw new SorushDisagreementException(String.format(ERROR_004, problemWorkingDays_bank.toString()));
		}
		
		if (!missingWorkingDays_shetab.isEmpty()) {
			throw new SorushDisagreementException(String.format(ERROR_003, missingWorkingDays_shetab.toString()));
		}
		if (!problemWorkingDays_shetab.isEmpty()) {
			throw new SorushDisagreementException(String.format(ERROR_004, problemWorkingDays_shetab.toString()));
		}		
	}

	private void fillWorkingsDays(final List<SorushRecord> sorushRecords, final List<String> allWorkingDays, final List<String> workingDays, final List<String> sorushWorkingDays) {
		int recordNumber = 1;
		for (SorushRecord record : sorushRecords) {
			if (!workingDays.contains(record.workingDay)) {
				workingDays.add(record.workingDay);
			}

			//Fill All Working Days
			if (!allWorkingDays.contains(record.workingDay)) {
				allWorkingDays.add(record.workingDay);
			}

			if (Util.hasText(record.sorushWorkingDay)) {
				if (!sorushWorkingDays.contains(record.sorushWorkingDay)) {
					sorushWorkingDays.add(record.sorushWorkingDay);
				}

				if (!allWorkingDays.contains(record.sorushWorkingDay)) {
					allWorkingDays.add(record.sorushWorkingDay);
				}
			} else {
				logger.info(String.format("INFO : soroushWorking Day is null - recordNumber is %s ", recordNumber));
			}

			recordNumber++;
		}
	}

	private List<SorushRecord> extractSorushRecords(final Information info, final File sorush, final Vector<String> v_error_tt_result, List<SorushRecord> sorushRecords, final Integer inputType) throws SorushDisagreementException, IOException {
		logger.info(String.format("start extract soroushRecord from %s", sorush));
		if (InputType.SORUSH_RES.equals(inputType)) {
			sorushRecords = getSorushRecord_from_file_sorushRes(sorush, info, v_error_tt_result);  ////Change in 92.12.21
		} else if (InputType.SORUSH_RES_AFTER_SANAD.equals(inputType)) {
			sorushRecords = getSorushRecord_from_file_sorushResAfterSanad(sorush, true, info, v_error_tt_result, false);  ////Change in 92.12.21
		}
		return sorushRecords;
	}

	private List<SorushRecord> extractSorushRecords(final Information info, final Vector<String> sorush, final Vector<String> v_error_tt_result, List<SorushRecord> sorushRecords, final Integer inputType, final Boolean skipCheckDateInCompare) throws FileNotFoundException {
		logger.info(String.format("start extract soroushRecord from %s", sorush));
		if (InputType.SORUSH_RES_AFTER_SANAD.equals(inputType)) {
			sorushRecords = getSorushRecord_from_buffer_sorushResAfterSanad(sorush, true, info, v_error_tt_result, skipCheckDateInCompare);
		}
		return sorushRecords;
	}

	public final void readFormsToCompare(final int threadId, final Map<String, BufferedReader> map, final Vector<SorushRecord> sorushRecords, final List<String> workingDays, final Boolean skipCheckDateInCompare) {
		try {
			Date date = null;
			String lastPersianWorkingDay = "";
			String nextPrsWorkingDay = "";
			List<SorushRecordCompact> shetabRecords;
			List<SorushRecordCompact> shetabRecords_balance;
			List<SorushRecordCompact> bankRecords;
			BufferedReader br;
			PersianDateFormat dateFormatPers = new PersianDateFormat("yyyyMMdd");

			List<String> validList = new ArrayList<String>();
			List<String> validList_balance = new ArrayList<String>();
			for (SorushRecord sorushRecord : sorushRecords) {
				if (sorushRecord.trxType.equals(BalanceInquery)) {
					validList_balance.add(sorushRecord.appPan);
				} else {
					validList.add(sorushRecord.appPan);
				}
			}

			for (String workingDay : workingDays) {
				for (String name : map.keySet()) {
					if (name.contains(workingDay)) {
						if (name.contains(SHETAB_POSTFIX)) { //shetab iss
							br = map.get(name);
							logger.info(String.format("Thread [%s] Process Shetab WorkingDay (%s) , FileName (%s)", threadId, workingDay, name));
							shetabRecords = getShetabRecordForSorush(threadId, br, name, validList);

							for (SorushRecord sorushRecord : sorushRecords) {
								if (!workingDay.equals(sorushRecord.workingDay)) {
									continue;
								}
								date = dateFormatPers.parse(sorushRecord.persianDt);
								lastPersianWorkingDay = dateFormatPers.format(new DayDate(date).previousDay().toDate());
								nextPrsWorkingDay = dateFormatPers.format(new DayDate(date).nextDay().toDate());

								if (!sorushRecord.trxType.equals(BalanceInquery)) {
									for (SorushRecordCompact shetabRecord : shetabRecords) {
										if (sorushRecord.trnSeqCntr.equals(shetabRecord.trnSeqCntr)
												&& sorushRecord.appPan.equals(shetabRecord.appPan)
												&& sorushRecord.bankId.equals(shetabRecord.bankId)
												&& (sorushRecord.persianDt.equals(shetabRecord.persianDt) || lastPersianWorkingDay.equals(shetabRecord.persianDt) || nextPrsWorkingDay.equals(shetabRecord.persianDt))) {
											sorushRecord.in8Shetab = true;
											break;
										}
										//AldComment Ezafe shod baraye halle moshkel notin8sh_notin8b
										if (skipCheckDateInCompare) {
											if (sorushRecord.trnSeqCntr.equals(shetabRecord.trnSeqCntr)
													&& sorushRecord.appPan.equals(shetabRecord.appPan)
													&& sorushRecord.bankId.equals(shetabRecord.bankId)) {
												sorushRecord.in8Shetab = true;
												logger.info("*In Shetab : " + sorushRecord.appPan + " | " + sorushRecord.trnSeqCntr + " | " + sorushRecord.bankId);
												break;
											}
										}
									}
								}
							}
						} else if (name.contains(SHETAB_POSTFIX_INQ)) { //shetab iss.inq
							br = map.get(name);
							logger.info(String.format("Thread [%s] Process Shetab inq WorkingDay (%s) , FileName (%s)", threadId, workingDay, name));
							shetabRecords_balance = getShetabRecordForSorush_inq(threadId, br, name, validList_balance);

							for (SorushRecord sorushRecord : sorushRecords) {
								if (!workingDay.equals(sorushRecord.workingDay)) {
									continue;
								}
								date = dateFormatPers.parse(sorushRecord.persianDt);
								lastPersianWorkingDay = dateFormatPers.format(new DayDate(date).previousDay().toDate());
								nextPrsWorkingDay = dateFormatPers.format(new DayDate(date).nextDay().toDate());

								if (sorushRecord.trxType.equals(BalanceInquery)) {
									for (SorushRecordCompact shetabRecord : shetabRecords_balance) {
										if (sorushRecord.trnSeqCntr.equals(shetabRecord.trnSeqCntr)
												&& sorushRecord.appPan.equals(shetabRecord.appPan)
												&& sorushRecord.bankId.equals(shetabRecord.bankId)
												&& (sorushRecord.persianDt.equals(shetabRecord.persianDt) || lastPersianWorkingDay.equals(shetabRecord.persianDt) || nextPrsWorkingDay.equals(shetabRecord.persianDt))) {
											sorushRecord.in8Shetab = true;
											break;
										}
										//AldComment Ezafe shod baraye halle moshkel notin8sh_notin8b
										if (skipCheckDateInCompare) {
											if (sorushRecord.trnSeqCntr.equals(shetabRecord.trnSeqCntr)
													&& sorushRecord.appPan.equals(shetabRecord.appPan)
													&& sorushRecord.bankId.equals(shetabRecord.bankId)) {
												sorushRecord.in8Shetab = true;
												logger.info("*In Shetab : " +  sorushRecord.appPan + " | " +  sorushRecord.trnSeqCntr + " | " + sorushRecord.bankId);
												break;
											}
										}
									}
								}
							}
						} else if (name.contains(BANK_POSTFIX)) { //for bank file
							br = map.get(name);
							logger.info(String.format("Thread [%s] Process Bank WorkingDay (%s) , FileName (%s)", threadId, workingDay, name));
							bankRecords = getBankRecordForSorush(threadId, br, name, validList); //null -> name
							for (SorushRecord sorushRecord : sorushRecords) {
								if (!workingDay.equals(sorushRecord.workingDay)) {
									continue;
								}
								date = dateFormatPers.parse(sorushRecord.persianDt);
								lastPersianWorkingDay = dateFormatPers.format(new DayDate(date).previousDay().toDate());
								nextPrsWorkingDay = dateFormatPers.format(new DayDate(date).nextDay().toDate());

								for (SorushRecordCompact bankRecord : bankRecords) {

									if (sorushRecord.trnSeqCntr.equals(bankRecord.trnSeqCntr)
											&& sorushRecord.appPan.equals(bankRecord.appPan)
											&& sorushRecord.bankId.equals(bankRecord.bankId)
											&& (sorushRecord.persianDt.equals(bankRecord.persianDt) || lastPersianWorkingDay.equals(bankRecord.persianDt) || nextPrsWorkingDay.equals(bankRecord.persianDt))) {
										sorushRecord.in8Bank = true;
										break;
									}
									//AldComment Ezafe shod baraye halle moshkel notin8sh_notin8b
									if (skipCheckDateInCompare) {
										if (sorushRecord.trnSeqCntr.equals(bankRecord.trnSeqCntr)
												&& sorushRecord.appPan.equals(bankRecord.appPan)
												&& sorushRecord.bankId.equals(bankRecord.bankId)) {
											sorushRecord.in8Bank = true;
											logger.info("*In Bank : " +  sorushRecord.appPan + " | " +  sorushRecord.trnSeqCntr + " | " + sorushRecord.bankId);
											break;
										}
									}
								}
							}
							bankRecords = null;
						}
					}
				}
			}

			//AldTODO : Remove this
//			logger.info("***********************");
//			for (SorushRecord sorushRecord : sorushRecords) {
//				logger.info(String.format("Apppan[%s] , in8sh[%s] , in8b[%s]", sorushRecord.appPan, sorushRecord.in8Shetab, sorushRecord.in8Bank));
//			}
//			logger.info("***********************");

			//AldComment Marhaleye Dovome Check
			for (SorushRecord sorushRecord : sorushRecords) {
				if (sorushRecord.in8Shetab && !sorushRecord.in8Bank && (sorushRecord.workingDay.equals(sorushRecord.sorushWorkingDay))) {
					for (String name : map.keySet()) {
						if (name.contains(sorushRecord.workingDay)) {
							if (name.contains(SHETAB_POSTFIX)) {
								br = map.get(name);
								logger.info(String.format("Marhaleye 2 Thread [%s] Process Shetab WorkingDay (%s) , FileName (%s) ", threadId, sorushRecord.workingDay, name));
								shetabRecords = getShetabRecordForSorush(threadId, br, null, validList);
								date = dateFormatPers.parse(sorushRecord.persianDt);
								lastPersianWorkingDay = dateFormatPers.format(new DayDate(date).previousDay().toDate());
								nextPrsWorkingDay = dateFormatPers.format(new DayDate(date).nextDay().toDate());
								for (SorushRecordCompact shetabRecord : shetabRecords) {
									if (sorushRecord.trnSeqCntr.equals(shetabRecord.trnSeqCntr)
											&& sorushRecord.appPan.equals(shetabRecord.appPan)
											&& (sorushRecord.persianDt.equals(shetabRecord.persianDt) || lastPersianWorkingDay.equals(shetabRecord.persianDt) || nextPrsWorkingDay.equals(shetabRecord.persianDt))) {
										sorushRecord.in8Shetab = true;
										break;
									}
									//AldComment Ezafe shod baraye halle moshkel notin8sh_notin8b
									if (skipCheckDateInCompare) {
										if (sorushRecord.trnSeqCntr.equals(shetabRecord.trnSeqCntr)
												&& sorushRecord.appPan.equals(shetabRecord.appPan)) {
											sorushRecord.in8Shetab = true;
											logger.info("*In Shetab Marhaleye2 : " +  sorushRecord.appPan + " | " +  sorushRecord.trnSeqCntr + " | " + sorushRecord.bankId);
											break;
										}
									}
								}
							} else if (name.contains(SHETAB_POSTFIX_INQ)) {
								br = map.get(name);
								logger.info(String.format("Marhaleye 2 Thread [%s] Process Shetab inq WorkingDay (%s) , FileName (%s) ", threadId, sorushRecord.workingDay, name));
								shetabRecords_balance = getShetabRecordForSorush_inq(threadId, br, null, validList_balance);
								date = dateFormatPers.parse(sorushRecord.persianDt);
								lastPersianWorkingDay = dateFormatPers.format(new DayDate(date).previousDay().toDate());
								nextPrsWorkingDay = dateFormatPers.format(new DayDate(date).nextDay().toDate());
								for (SorushRecordCompact shetabRecord : shetabRecords_balance) {
									if (sorushRecord.trnSeqCntr.equals(shetabRecord.trnSeqCntr)
											&& sorushRecord.appPan.equals(shetabRecord.appPan)
											&& (sorushRecord.persianDt.equals(shetabRecord.persianDt) || lastPersianWorkingDay.equals(shetabRecord.persianDt) || nextPrsWorkingDay.equals(shetabRecord.persianDt))) {
										sorushRecord.in8Shetab = true;
										break;
									}
									//AldComment Ezafe shod baraye halle moshkel notin8sh_notin8b
									if (skipCheckDateInCompare) {
										if (sorushRecord.trnSeqCntr.equals(shetabRecord.trnSeqCntr)
												&& sorushRecord.appPan.equals(shetabRecord.appPan)) {
											sorushRecord.in8Shetab = true;
											logger.info("*In Shetab Marhaleye2 : " +  sorushRecord.appPan + " | " +  sorushRecord.trnSeqCntr + " | " + sorushRecord.bankId);
											break;
										}
									}
								}
							} else if (name.contains(BANK_POSTFIX)) {
								br = map.get(name);
								logger.info(String.format("Marhaleye 2 Thread [%s] Process Bank WorkingDay (%s) , FileName (%s)", threadId, sorushRecord.workingDay, name));
								bankRecords = getBankRecordForSorush(threadId, br, null, validList);
								date = dateFormatPers.parse(sorushRecord.persianDt);
								lastPersianWorkingDay = dateFormatPers.format(new DayDate(date).previousDay().toDate());
								nextPrsWorkingDay = dateFormatPers.format(new DayDate(date).nextDay().toDate());
								for (SorushRecordCompact bankRecord : bankRecords) {

									if (sorushRecord.trnSeqCntr.equals(bankRecord.trnSeqCntr)
											&& sorushRecord.appPan.equals(bankRecord.appPan)
											&& (sorushRecord.persianDt.equals(bankRecord.persianDt) || lastPersianWorkingDay.equals(bankRecord.persianDt) || nextPrsWorkingDay.equals(bankRecord.persianDt))) {
										sorushRecord.in8Bank = true;
										sorushRecord.isNotValid_in8Sh_in8b = true; //AldComment
										logger.debug(String.format("Marhaleye 2 Find apppan : %s , seqCntr : %s , soroush.PersianDt : %s , amount : %s", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.persianDt, sorushRecord.amount));
										break;
									}
									//AldComment Ezafe shod baraye halle moshkel notin8sh_notin8b
									if (skipCheckDateInCompare) {
										if (sorushRecord.trnSeqCntr.equals(bankRecord.trnSeqCntr)
												&& sorushRecord.appPan.equals(bankRecord.appPan)) {
											sorushRecord.in8Bank = true;
											sorushRecord.isNotValid_in8Sh_in8b = true; //AldComment
											logger.debug(String.format("Marhaleye 2 Find apppan : %s , seqCntr : %s , soroush.PersianDt : %s , amount : %s", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.persianDt, sorushRecord.amount));
											logger.info("*In Bank Marhaleye2 : " +  sorushRecord.appPan + " | " +  sorushRecord.trnSeqCntr + " | " + sorushRecord.bankId);
											break;
										}
									}
								}
							bankRecords = null;
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			logger.error(String.format("Thread [" + threadId + "] error in readFormsToComapare", threadId));
		}

	}

	public final void readFormsToCompareForSoroush(final int threadId, final Map<String, BufferedReader> map, final Vector<SorushRecord> sorushRecords, final List<String> sorushWorkingDays, final Boolean skipCheckDateInCompare) {
		try {
			MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage(); //test
			Date date = null;
			String lastPersianWorkingDay = "";
			String nextPrsWorkingDay = "";
			List<SorushRecordCompact> shetabRecords;
			List<SorushRecordCompact> bankRecords;
			BufferedReader br;
			PersianDateFormat dateFormatPers = new PersianDateFormat("yyyyMMdd");

			List<String> validList = new ArrayList<String>();
			for (SorushRecord sorushRecord : sorushRecords) {
				validList.add(sorushRecord.appPan);
			}

			for (String sorushWorkingDay : sorushWorkingDays) {
				for (String name : map.keySet()) {
					if (name.contains(sorushWorkingDay)) {
						if (name.contains(SHETAB_POSTFIX)) {
							br = map.get(name);
							logger.info(String.format("Thread [%s] Process Shetab WorkingDay (%s) , FileName (%s) , Memory Used (%s)", threadId, sorushWorkingDay, name, heapMemoryUsage.getUsed()));
							shetabRecords = getShetabRecordForSorush(threadId, br, name, validList);
							for (SorushRecord sorushRecord : sorushRecords) {
								if (!sorushWorkingDay.equals(sorushRecord.sorushWorkingDay)) {
									continue;
								}

//								if (!sorushWorkingDay.equals(sorushRecord.sorushWorkingDay) /* || !sorushRecord.in8Bank || !sorushRecord.in8Shetab */ )
//									continue;

								date = dateFormatPers.parse(sorushRecord.sorushPersianDt);
								lastPersianWorkingDay = dateFormatPers.format(new DayDate(date).previousDay().toDate());
								nextPrsWorkingDay = dateFormatPers.format(new DayDate(date).nextDay().toDate());
								for (SorushRecordCompact shetabRecord : shetabRecords) {
									if (sorushRecord.sorushTrnSeqCntr.equals(shetabRecord.trnSeqCntr)
											&& sorushRecord.appPan.equals(shetabRecord.appPan)
											&& shetabRecord.bankId.equals(SORUSH_BIN)
											&& (sorushRecord.sorushPersianDt.equals(shetabRecord.persianDt) || lastPersianWorkingDay.equals(shetabRecord.persianDt) || nextPrsWorkingDay.equals(shetabRecord.persianDt))) {
										sorushRecord.sorushIn8Shetab = true;
										break;
									}
									//AldComment Ezafe shod baraye halle moshkel notin8sh_notin8b
									if (skipCheckDateInCompare) {
										if (sorushRecord.sorushTrnSeqCntr.equals(shetabRecord.trnSeqCntr)
												&& sorushRecord.appPan.equals(shetabRecord.appPan)
												&& shetabRecord.bankId.equals(SORUSH_BIN)) {
											sorushRecord.sorushIn8Shetab = true;
											logger.info("*In Shetab Sorush : " +  sorushRecord.appPan + " | " +  sorushRecord.trnSeqCntr + " | " + sorushRecord.bankId);
											break;
										}
									}
								}
							}
						} else if (name.contains(BANK_POSTFIX)) {
							br = map.get(name);
							logger.info(String.format("Thread [%s] Process Bank WorkingDay (%s) , FileName (%s) , Memory Used (%s)", threadId, sorushWorkingDay, name, heapMemoryUsage.getUsed()));
							bankRecords = getBankRecordForSorush(threadId, br, name, validList);
							for (SorushRecord sorushRecord : sorushRecords) {
								if (!sorushWorkingDay.equals(sorushRecord.sorushWorkingDay)) {
									continue;
								}

//								if (!sorushWorkingDay.equals(sorushRecord.sorushWorkingDay) /* || !sorushRecord.in8Bank || !sorushRecord.in8Shetab */)
//									continue;

								date = dateFormatPers.parse(sorushRecord.sorushPersianDt);
								lastPersianWorkingDay = dateFormatPers.format(new DayDate(date).previousDay().toDate());
								nextPrsWorkingDay = dateFormatPers.format(new DayDate(date).nextDay().toDate());
								for (SorushRecordCompact bankRecord : bankRecords) {

									if (sorushRecord.sorushTrnSeqCntr.equals(bankRecord.trnSeqCntr)
											&& sorushRecord.appPan.equals(bankRecord.appPan)
											&& bankRecord.bankId.equals(SORUSH_BIN)
											&& (sorushRecord.sorushPersianDt.equals(bankRecord.persianDt) || lastPersianWorkingDay.equals(bankRecord.persianDt) || nextPrsWorkingDay.equals(bankRecord.persianDt))) {
										sorushRecord.soroushIn8Bank = true;
										break;
									}
									//AldComment Ezafe shod baraye halle moshkel notin8sh_notin8b
									if (skipCheckDateInCompare) {
										if (sorushRecord.sorushTrnSeqCntr.equals(bankRecord.trnSeqCntr)
												&& sorushRecord.appPan.equals(bankRecord.appPan)
												&& bankRecord.bankId.equals(SORUSH_BIN)) {
											sorushRecord.soroushIn8Bank = true;
											logger.info("*In Bank Sorush : " +  sorushRecord.appPan + " | " +  sorushRecord.trnSeqCntr + " | " + sorushRecord.bankId);
											break;
										}
									}
								}
							}
							bankRecords = null;
						}
					}
				}
			}
		} catch (Exception e) {

		}

	}

	public final void processSoroushRecords(final Information info, final Boolean skipCheckDateInCompare, final Vector<String> sanad_result, final Vector<String> error_result, final Vector<String> other_result, final Vector<String> sanad_error_result, final List<SorushRecord> sorushRecords)
			throws IOException {
		processSoroushRecords_self(info, skipCheckDateInCompare, error_result, sorushRecords);

		processSoroushRecords_in8sh_in8b(info, skipCheckDateInCompare, sanad_result, error_result, other_result, sorushRecords);

		processSoroushRecords_notin8sh_in8b(info, skipCheckDateInCompare, sanad_result, error_result, other_result, sorushRecords);

		//get list of sanad nakhorder
		synchronized (sanad_result) {
			for (String record : sanad_result) {
				String[] feilds = record.split("[|]");
				if (feilds[feilds.length - 2].toLowerCase().equals("null") || !Util.hasText(feilds[feilds.length - 2])) {
					if (!sanad_error_result.contains(record)) {
						sanad_error_result.add(record);
					}
				}
			}
		}
	}

	private void processSoroushRecords_self(final Information info, final Boolean skipCheckDateInCompare,
			final Vector<String> error_result, final List<SorushRecord> sorushRecords) {
		try {
			String transactionId = null;
			for (SorushRecord sorushRecord : sorushRecords) {
				transactionId = null;
				if (!sorushRecord.in8Shetab && !sorushRecord.in8Bank) { //notin8sh_notin8b_self
					error_result.add(sorushRecord.data + "|" + transactionId + "|" + getResultString("notin8sh_notin8b", sorushRecord, false));
				} else if (sorushRecord.in8Shetab && !sorushRecord.in8Bank) { //in8sh_notin8b_self
					error_result.add(sorushRecord.data + "|" + transactionId + "|" + getResultString("in8sh_notin8b", sorushRecord, false));
				} else if (!sorushRecord.in8Shetab && sorushRecord.in8Bank) { //notin8sh_in8b_self
					if (TrnType.INCREMENTALTRANSFER.toString().equals(sorushRecord.trxType)) {
						error_result.add(sorushRecord.data + "|" + transactionId + "|" + "notin8sh_in8b_tt");
					}
				} else if (sorushRecord.in8Shetab && sorushRecord.in8Bank) { //in8sh_in8b_self
					if (TrnType.INCREMENTALTRANSFER.toString().equals(sorushRecord.trxType)) {
						error_result.add(sorushRecord.data + "|" + transactionId + "|" + "in8sh_in8b_tt");
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Exception in thread [" + info.getThreadId() + "] " + ex.getMessage());
		}
	}
	
	private void processSoroushRecords_in8sh_in8b(final Information info, final Boolean skipCheckDateInCompare,
			final Vector<String> sanad_result, final Vector<String> error_result,
			final Vector<String> other_result, final List<SorushRecord> sorushRecords) {
		try {
			String transactionId = null;
			for (SorushRecord sorushRecord : sorushRecords) {
				transactionId = null;

				//For Soursuh  
				if (!sorushRecord.in8Shetab || !sorushRecord.in8Bank) {
					continue;
				}
				//in8sh_in8b_self_in8sh_in8b_sorush
				if (sorushRecord.sorushIn8Shetab && sorushRecord.soroushIn8Bank) {
					if (!TrnType.INCREMENTALTRANSFER.toString().equals(sorushRecord.trxType)) {
						if (sorushRecord.state_of_I_O.equals("O")) {
							if (TransactionType.EXTERNAL.equals(sorushRecord.sorushTransactionType)) {
								other_result.add(sorushRecord.data + "|" + transactionId + "|" + getResultString("in8sh_in8b_self_in8sh_in8b_sorush", sorushRecord, false)); //in8sh_in8b_self_in8sh_in8b_sorush_O_EXTERNAL
							} else {
								error_result.add(sorushRecord.data + "|" + transactionId + "|" + getResultString("in8sh_in8b_self_in8sh_in8b_sorush", sorushRecord, false)); //in8sh_in8b_self_in8sh_in8b_sorush_O_SELF
							}
						} else if (sorushRecord.state_of_I_O.equals("I")) {
							error_result.add(sorushRecord.data + "|" + transactionId + "|" + getResultString("in8sh_in8b_self_in8sh_in8b_sorush", sorushRecord, false)); //in8sh_in8b_self_in8sh_in8b_sorush_O_SELF
						} else {
							logger.error(String.format("ERROR_STATE_OF_I_O [in8sh_in8b_self_in8sh_in8b_sorush] %s",sorushRecord.data));
						}
					} 
				}
				
				//in8sh_in8b_self_notin8sh_in8b_sorush
				if (!sorushRecord.sorushIn8Shetab && sorushRecord.soroushIn8Bank) {
					if (!TrnType.INCREMENTALTRANSFER.toString().equals(sorushRecord.trxType)) {
						if (sorushRecord.state_of_I_O.equals("O")) {
							if (TransactionType.EXTERNAL.equals(sorushRecord.sorushTransactionType)) {
								transactionId = sanad_Pattern1(sorushRecord, getResultString("in8sh_in8b_self_notin8sh_in8b_sorush", sorushRecord, false), sorushRecord.sorushFileDate);
								sanad_result.add(sorushRecord.data + "|" + transactionId + "|" + getResultString("in8sh_in8b_self_notin8sh_in8b_sorush", sorushRecord, false)); //in8sh_in8b_self_in8sh_in8b_sorush_O_EXTERNAL
							} else {
								error_result.add(sorushRecord.data + "|" + transactionId + "|" + getResultString("in8sh_in8b_self_notin8sh_in8b_sorush", sorushRecord, false)); //in8sh_in8b_self_in8sh_in8b_sorush_O_SELF
							}
						} else if (sorushRecord.state_of_I_O.equals("I")) {
							error_result.add(sorushRecord.data + "|" + transactionId + "|" + getResultString("in8sh_in8b_self_notin8sh_in8b_sorush", sorushRecord, false)); //in8sh_in8b_self_in8sh_in8b_sorush_O_SELF
						} else {
							logger.error(String.format("ERROR_STATE_OF_I_O [in8sh_in8b_self_in8sh_in8b_sorush] %s",sorushRecord.data));
						}
					} 
				}
				
				if (sorushRecord.sorushIn8Shetab && !sorushRecord.soroushIn8Bank) {
					if (!TrnType.INCREMENTALTRANSFER.toString().equals(sorushRecord.trxType)) {
						error_result.add(sorushRecord.data + "|" + transactionId + "|" + getResultString("in8sh_in8b_self_in8sh_notin8b_sorush", sorushRecord, false));
					}
				}

				if (!sorushRecord.sorushIn8Shetab && !sorushRecord.soroushIn8Bank) {
					if (!TrnType.INCREMENTALTRANSFER.toString().equals(sorushRecord.trxType)) {
						transactionId = sanad_Pattern2(sorushRecord, getResultString("in8sh_in8b_self_notin8sh_notin8b_sorush", sorushRecord, false), sorushRecord.sorushFileDate);
						sanad_result.add(sorushRecord.data  + "|" + transactionId + "|" + getResultString("in8sh_in8b_self_notin8sh_notin8b_sorush", sorushRecord, false));
					}
				}
				
			}
		} catch (Exception ex) {
			logger.error("Exception in thread [" + info.getThreadId() + "] " + ex.getMessage());
		}
	}

	private void processSoroushRecords_notin8sh_in8b(final Information info, final Boolean skipCheckDateInCompare,
			final Vector<String> sanad_result, final Vector<String> error_result,
			final Vector<String> other_result, final List<SorushRecord> sorushRecords) {
		//**** For notin8sh_in8b check soroush 
		try {
			String transactionId = null;
			for (SorushRecord sorushRecord : sorushRecords) {
				transactionId = null;

				//For Soursuh
				if (sorushRecord.in8Shetab || !sorushRecord.in8Bank) {
					continue;
				}
				if (sorushRecord.sorushIn8Shetab && sorushRecord.soroushIn8Bank) {
					if (!TrnType.INCREMENTALTRANSFER.toString().equals(sorushRecord.trxType)) {
						transactionId = sanad_Pattern3(sorushRecord, getResultString("notin8sh_in8b_self_in8sh_in8b_sorush", sorushRecord, false), sorushRecord.sorushFileDate);
						sanad_result.add(sorushRecord.data + "|" + transactionId + "|" + getResultString("notin8sh_in8b_self_in8sh_in8b_sorush", sorushRecord, false));
					}
				}
				if (!sorushRecord.sorushIn8Shetab && sorushRecord.soroushIn8Bank) {
					if (!TrnType.INCREMENTALTRANSFER.toString().equals(sorushRecord.trxType)) {
						other_result.add(sorushRecord.data + "|" + transactionId + "|" +  getResultString("notin8sh_in8b_self_notin8sh_in8b_sorush", sorushRecord, false));
					}
				}
				if (sorushRecord.sorushIn8Shetab && !sorushRecord.soroushIn8Bank) {
					if (!TrnType.INCREMENTALTRANSFER.toString().equals(sorushRecord.trxType)) {
						error_result.add(sorushRecord.data + "|" + transactionId + "|" + getResultString("notin8sh_in8b_self_in8sh_notin8b_sorush", sorushRecord, false));
					}
				}				
				if (!sorushRecord.sorushIn8Shetab && !sorushRecord.soroushIn8Bank) {
					if (!TrnType.INCREMENTALTRANSFER.toString().equals(sorushRecord.trxType)) {
						transactionId = sanad_Pattern4(sorushRecord, getResultString("notin8sh_in8b_self_notin8sh_notin8b_sorush", sorushRecord, false), sorushRecord.sorushFileDate);
						sanad_result.add(sorushRecord.data + "|" + transactionId + "|" + getResultString("notin8sh_in8b_self_notin8sh_notin8b_sorush", sorushRecord, false));
					} 
				}
				
			}
		} catch (Exception ex) {
			logger.error("Exception in thread [" + info.getThreadId() + "] " + ex.getMessage());
		}
	}

	private String getResultString(String desc, final SorushRecord sorushRecord, final boolean isStar) {
		if (TrnType.DECREMENTALTRANSFER.toString().equals(sorushRecord.trxType)) {
			desc  += "_tf";
		} else if (TrnType.INCREMENTALTRANSFER.toString().equals(sorushRecord.trxType)) {
			desc  += "_tt";
		} 
		return desc + (isStar ? ";***" : "");
	}
	
	private String sanad_Pattern1(final SorushRecord sorushRecord, final String postDescription , final String date) {
		if (!createXMLDocument)
		 {
			return "null"; //test
		}
		
		String description = "مغایرت گیری سروش از مغایرت شتاب به ما به التفاوت " + " " + postDescription;
	    String bandeSanadDesc = String.format("شماره کارت : %s شماره پیگیری : %s روز کاری : %s ", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.workingDay);
		DocumentItemEntity debtorDocumentEntity = null;
		DocumentItemEntity creditorDoccumentEntity = null;		
		List<DocumentItemEntity> docs = new ArrayList<DocumentItemEntity>();
		debtorDocumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), true, FANAP_SWITCH_ID, bandeSanadDesc, BESTANKARANE_MOGHAYERATE_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);
		creditorDoccumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), false, FANAP_SWITCH_ID, bandeSanadDesc, MABEOTAFAVOT, IssueGeneralDocVO.DocumentItemEntityType.Account);					
		docs.add(debtorDocumentEntity);
		docs.add(creditorDoccumentEntity);	
		return sanad(docs, sorushRecord, description, date);	
	}	
	
	private String sanad_Pattern2(final SorushRecord sorushRecord, final String postDescription , final String date) {
		if (!createXMLDocument) {
			return "null"; //test
		}

		
		String description = "مغایرت گیری سروش از مغایرت شتاب به واسط شتاب " + " " + postDescription;
	    String bandeSanadDesc = String.format("شماره کارت : %s شماره پیگیری : %s روز کاری : %s ", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.workingDay);
		DocumentItemEntity debtorDocumentEntity = null;
		DocumentItemEntity creditorDoccumentEntity = null;		
		List<DocumentItemEntity> docs = new ArrayList<DocumentItemEntity>();
		debtorDocumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), true, FANAP_SWITCH_ID, bandeSanadDesc, BESTANKARANE_MOGHAYERATE_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);
		creditorDoccumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), false, FANAP_SWITCH_ID, bandeSanadDesc, VASETE_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);					
		docs.add(debtorDocumentEntity);
		docs.add(creditorDoccumentEntity);	
		return sanad(docs, sorushRecord, description, date);	
	}

	private String sanad_Pattern3(final SorushRecord sorushRecord, final String postDescription , final String date) {
		if (!createXMLDocument) {
			return "null"; //test
		}
		
		String description = "مغایرت گیری سروش از ما به التفاوت به مغایرت شتاب " + " " + postDescription;
	    String bandeSanadDesc = String.format("شماره کارت : %s شماره پیگیری : %s روز کاری : %s ", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.workingDay);
		DocumentItemEntity debtorDocumentEntity = null;
		DocumentItemEntity creditorDoccumentEntity = null;		
		List<DocumentItemEntity> docs = new ArrayList<DocumentItemEntity>();
		debtorDocumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), true, FANAP_SWITCH_ID, bandeSanadDesc, MABEOTAFAVOT, IssueGeneralDocVO.DocumentItemEntityType.Account);
		creditorDoccumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), false, FANAP_SWITCH_ID, bandeSanadDesc, BESTANKARANE_MOGHAYERATE_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);					
		docs.add(debtorDocumentEntity);
		docs.add(creditorDoccumentEntity);	
		return sanad(docs, sorushRecord, description, date);
	}

	private String sanad_Pattern4(final SorushRecord sorushRecord, final String postDescription , final String date) {
		if (!createXMLDocument) {
			return "null"; //test
		}
		
		String description = "مغایرت گیری سروش از ما به التفاوت به واسط شتاب " + " " + postDescription;
	    String bandeSanadDesc = String.format("شماره کارت : %s شماره پیگیری : %s روز کاری : %s ", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.workingDay);
		DocumentItemEntity debtorDocumentEntity = null;
		DocumentItemEntity creditorDoccumentEntity = null;		
		DocumentItemEntity debtorDocumentEntity2 = null;
		DocumentItemEntity creditorDoccumentEntity2 = null;		
		List<DocumentItemEntity> docs = new ArrayList<DocumentItemEntity>();
		debtorDocumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), true, FANAP_SWITCH_ID, bandeSanadDesc, MABEOTAFAVOT, IssueGeneralDocVO.DocumentItemEntityType.Account);
		creditorDoccumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), false, FANAP_SWITCH_ID, bandeSanadDesc, BESTANKARANE_MOGHAYERATE_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);					
		debtorDocumentEntity2 = new DocumentItemEntity(new Double(sorushRecord.amount), true, FANAP_SWITCH_ID, bandeSanadDesc, BESTANKARANE_MOGHAYERATE_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);
		creditorDoccumentEntity2 = new DocumentItemEntity(new Double(sorushRecord.amount), false, FANAP_SWITCH_ID, bandeSanadDesc, VASETE_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);					
		docs.add(debtorDocumentEntity);
		docs.add(creditorDoccumentEntity);	
		docs.add(debtorDocumentEntity2);
		docs.add(creditorDoccumentEntity2);	
		return sanad(docs, sorushRecord, description, date);	}	

	private String sanad_Pattern5(final SorushRecord sorushRecord, final String postDescription , final String date) {
		if (!createXMLDocument) {
			return "null"; //test
		}

		String description = "مغایرت گیری سروش از واسط شتاب به مابه التفاوت " + " " + postDescription;
	    String bandeSanadDesc1 = String.format("معادل سند Repbal بابت تراکنش با شماره کارت : %s شماره پیگیری : %s روز کاری : %s", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.workingDay);
	    String bandeSanadDesc2 = String.format("معادل سند شتابی بابت تراکنش با شماره کارت : %s شماره پیگیری : %s روز کاری : %s", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.workingDay);
		DocumentItemEntity debtorDocumentEntity = null;
		DocumentItemEntity creditorDoccumentEntity = null;		
		DocumentItemEntity debtorDocumentEntity2 = null;
		DocumentItemEntity creditorDoccumentEntity2 = null;		
		List<DocumentItemEntity> docs = new ArrayList<DocumentItemEntity>();
		debtorDocumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), true, FANAP_SWITCH_ID, bandeSanadDesc1, DAKHELI_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);
		creditorDoccumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), false, FANAP_SWITCH_ID, bandeSanadDesc1, MABEOTAFAVOT, IssueGeneralDocVO.DocumentItemEntityType.Account);					
		debtorDocumentEntity2 = new DocumentItemEntity(new Double(sorushRecord.amount), true, FANAP_SWITCH_ID, bandeSanadDesc2, VASETE_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);
		creditorDoccumentEntity2 = new DocumentItemEntity(new Double(sorushRecord.amount), false, FANAP_SWITCH_ID, bandeSanadDesc2, DAKHELI_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);					
		docs.add(debtorDocumentEntity);
		docs.add(creditorDoccumentEntity);	
		docs.add(debtorDocumentEntity2);
		docs.add(creditorDoccumentEntity2);	
		return sanad(docs, sorushRecord, description, date);
	}

	private String sanad_Pattern6(final SorushRecord sorushRecord, final String postDescription , final String date) {
		if (!createXMLDocument) {
			return "null"; //test
		}

		String description = "مغایرت گیری سروش از واسط شتاب به مابه التفاوت بابت اصل تراکنش " + " " + postDescription;
	    String bandeSanadDesc1 = String.format("معادل سند Repbal بابت تراکنش با شماره کارت : %s شماره پیگیری : %s روز کاری : %s", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.workingDay);
	    String bandeSanadDesc2 = String.format("معادل سند شتابی بابت تراکنش با شماره کارت : %s شماره پیگیری : %s روز کاری : %s", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.workingDay);
		DocumentItemEntity debtorDocumentEntity = null;
		DocumentItemEntity creditorDoccumentEntity = null;
		DocumentItemEntity debtorDocumentEntity2 = null;
		DocumentItemEntity creditorDoccumentEntity2 = null;		
		List<DocumentItemEntity> docs = new ArrayList<DocumentItemEntity>();
		debtorDocumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), true, FANAP_SWITCH_ID, bandeSanadDesc1, DAKHELI_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);
		creditorDoccumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), false, FANAP_SWITCH_ID, bandeSanadDesc1, MABEOTAFAVOT, IssueGeneralDocVO.DocumentItemEntityType.Account);					
		debtorDocumentEntity2 = new DocumentItemEntity(new Double(sorushRecord.amount), true, FANAP_SWITCH_ID, bandeSanadDesc2, VASETE_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);
		creditorDoccumentEntity2 = new DocumentItemEntity(new Double(sorushRecord.amount), false, FANAP_SWITCH_ID, bandeSanadDesc2, DAKHELI_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);					
		docs.add(debtorDocumentEntity);
		docs.add(creditorDoccumentEntity);	
		docs.add(debtorDocumentEntity2);
		docs.add(creditorDoccumentEntity2);	
		String documentId1 = sanad(docs, sorushRecord, description, date);	
		
		if (Util.hasText(documentId1)) {
			description = "مغایرت گیری سروش از مغایرت شتاب به ما به التفاوت بابت تراکنش سروشی" + " " + postDescription;
		    String bandeSanadDesc = String.format("شماره کارت : %s شماره پیگیری : %s روز کاری : %s ", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.workingDay);
		    docs.clear();
			debtorDocumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), true, FANAP_SWITCH_ID, bandeSanadDesc, BESTANKARANE_MOGHAYERATE_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);
			creditorDoccumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), false, FANAP_SWITCH_ID, bandeSanadDesc, MABEOTAFAVOT, IssueGeneralDocVO.DocumentItemEntityType.Account);					
			docs.add(debtorDocumentEntity);
			docs.add(creditorDoccumentEntity);	
			String documentId2 =  sanad(docs, sorushRecord, description, date, "ASNADE_TAKMILIE_SOROSHE_92_ESLAHIEH-P2-");	//P2 means Part 2
			
			return documentId1 + ";" + documentId2;
		} else {
			return documentId1;
		}
	}	

	@NotUsed
	private String sanad_Pattern7(final SorushRecord sorushRecord, final String postDescription , final String date) {
		
		if (!createXMLDocument) {
			return "null"; //test
		}

		String description = "مغایرت گیری سروش از مغایرت شتاب به ما به التفاوت " + " " + postDescription;
	    String bandeSanadDesc1 = String.format("معادل سند Repbal بابت تراکنش با شماره کارت : %s شماره پیگیری : %s روز کاری : %s", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.workingDay);
	    String bandeSanadDesc2 = String.format("معادل سند شتابی بابت تراکنش با شماره کارت : %s شماره پیگیری : %s روز کاری : %s", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.workingDay);
		DocumentItemEntity debtorDocumentEntity = null;
		DocumentItemEntity creditorDoccumentEntity = null;		
		DocumentItemEntity debtorDocumentEntity2 = null;
		DocumentItemEntity creditorDoccumentEntity2 = null;		
		List<DocumentItemEntity> docs = new ArrayList<DocumentItemEntity>();
		debtorDocumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), true, FANAP_SWITCH_ID, bandeSanadDesc1, DAKHELI_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);
		creditorDoccumentEntity = new DocumentItemEntity(new Double(sorushRecord.amount), false, FANAP_SWITCH_ID, bandeSanadDesc1, MABEOTAFAVOT, IssueGeneralDocVO.DocumentItemEntityType.Account);					
		debtorDocumentEntity2 = new DocumentItemEntity(new Double(sorushRecord.amount), true, FANAP_SWITCH_ID, bandeSanadDesc2, BESTANKARANE_MOGHAYERATE_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);
		creditorDoccumentEntity2 = new DocumentItemEntity(new Double(sorushRecord.amount), false, FANAP_SWITCH_ID, bandeSanadDesc2, DAKHELI_SHETAB, IssueGeneralDocVO.DocumentItemEntityType.Account);					
		docs.add(debtorDocumentEntity);
		docs.add(creditorDoccumentEntity);	
		docs.add(debtorDocumentEntity2);
		docs.add(creditorDoccumentEntity2);	
		return sanad(docs, sorushRecord, description, date);		
	}	
	
	private List<String> readZipContasinForm8Bank(final File folder, final Map<String, BufferedReader>  map, final String password) throws IOException {
		List<String> brNames = new ArrayList<String>();
		for (File file : folder.listFiles()) {
			if (file.getName().contains(".zip")) {
				if (file.getName().toLowerCase().contains(BANK_NAME.toLowerCase())) {
					net.lingala.zip4j.core.ZipFile bankZip;
					try {
						bankZip = new net.lingala.zip4j.core.ZipFile(file);
						List<?> fileHeaderList = bankZip.getFileHeaders();
						for (int i = 0; i < fileHeaderList.size(); i++) {
							FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
							if (fileHeader.getFileName().toLowerCase().startsWith(_PAS.toLowerCase()) && !fileHeader.getFileName().toLowerCase().startsWith(_PASREP.toLowerCase()) && !fileHeader.getFileName().toLowerCase().startsWith(_PAS2REP.toLowerCase())) {
					            if (bankZip.isEncrypted()) {
//					            	String pasword = Reverse(fileHeader.getFileName());
//					            	password = Reverse(password.substring(4,10));
//					            	bankZip.setPassword(fileHeader.getFileName().substring(3,9));
					            	bankZip.setPassword(password);
					            }
								brNames.add(folder.getName() + BANK_POSTFIX + fileHeader.getFileName());
								map.put(folder.getName() + BANK_POSTFIX + fileHeader.getFileName(), new BufferedReader(new InputStreamReader(bankZip.getInputStream(fileHeader)/*inputStream*/)));
							}
						}
					} catch (ZipException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return brNames;
	}

	private List<String> readZipContasinForm8Shetab(final File folder, final Map<String, BufferedReader>  map, final String password) throws IOException {
		List<String> brNames = new ArrayList<String>();
		for (File file : folder.listFiles()) {
			if (file.getName().contains(".zip")) {
				if (file.getName().toLowerCase().contains(SHETAB_NAME.toLowerCase())) {
					ZipFile shetabZip = new ZipFile(file);
					Enumeration<? extends ZipEntry> entries = shetabZip.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						if (entry.getName().toLowerCase().endsWith(_ISS) && entry.getName().toLowerCase().contains(_IRI)) {
							brNames.add(folder.getName() + SHETAB_POSTFIX + entry.getName());
							map.put(folder.getName() + SHETAB_POSTFIX + entry.getName(), new BufferedReader(new InputStreamReader(shetabZip.getInputStream(entry))));
						}
						if (entry.getName().toLowerCase().endsWith(_ISS_INQ) && entry.getName().toLowerCase().contains(_IRI)) {
							brNames.add(folder.getName() + SHETAB_POSTFIX_INQ + entry.getName());
							map.put(folder.getName() + SHETAB_POSTFIX_INQ + entry.getName(), new BufferedReader(new InputStreamReader(shetabZip.getInputStream(entry))));
						}
					}
				}
			}
		}
		return brNames;
	}

	
	private File unZip(final String zipFile, final String outputFolder) throws SorushDisagreementException {
		try {
	      ZipInputStream zin = new ZipInputStream(new FileInputStream(new File(zipFile)));
	      if (!isZip(new File(zipFile))) {
	    	  throw new SorushDisagreementException(ERROR_002);
	      }
	      ZipEntry entry;
	      String name, dir;
	      File outDir = new File(outputFolder);
	      while ((entry = zin.getNextEntry()) != null) {
	        name = entry.getName();
	        if (entry.isDirectory()) {
	          mkdirs(outDir, name);
	          continue;
	        }
	        /* this part is necessary because file entry can come before
	         * directory entry where is file located
	         * i.e.:
	         *   /foo/foo.txt
	         *   /foo/
	         */
	        dir = dirpart(name);
	        if (dir != null) {
				mkdirs(outDir, dir);
			}

	        extractFile(zin, outDir, name);
	      }
	      zin.close();
	      return outDir;
		} catch (IOException e) {
	      e.printStackTrace();
	      return null;
	    }
	}

	private Vector<SorushRecord> getSorushRecord_from_file_sorush(final File sorushFile, final Information info, final Boolean onlyForErrors_ChangeBins) throws SorushDisagreementException, IOException {
		BufferedReader br =  new BufferedReader(new FileReader(sorushFile));
		PersianDateFormat dateFormatPers = new PersianDateFormat("yyyyMMdd");
		String line = "";
		SorushRecord record = null;
		Vector<SorushRecord> result = new Vector<SorushRecord>();
		StringTokenizer tokenizer;
		Long row = 0L;
		try {
			while (br.ready()) {
				if ((line = br.readLine()).length() > 0) {
					row++;
					logger.info("sorush" + row + ": " + line);
					record = new SorushRecord();
					record.data = line;
					tokenizer = new StringTokenizer(line, "|");
					record.sorushFileDate = info.getDate();

					record.persianDt =  "13" + tokenizer.nextToken().trim();
					Date date = dateFormatPers.parse(record.persianDt);
					record.trxDate = new DayDate(date);
					record.trnSeqCntr = tokenizer.nextToken().trim();
					if (record.trnSeqCntr != null && !record.trnSeqCntr.equals("null")) {
						record.trnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, record.trnSeqCntr, '0');
					}
					record.appPan = tokenizer.nextToken().trim();
					tokenizer.nextToken();
					record.bankId = Long.valueOf(tokenizer.nextToken().trim());

					//AldComment baraye inke tedadi az all errors ha ra kam konim barkhai az bin bank ha ra agar ba bin psp anha begardim hal mishavad 
					// in tekke code baraye in ejad shod ke yek tedadi az all errors ha ra kam konim
					if (onlyForErrors_ChangeBins) {
						changeBankId(record);
					}
					
					for (int i = 0; i < 2; i++) {
						tokenizer.nextToken();
					}

					String state_of_n_nn = tokenizer.nextToken().trim().toLowerCase();
					String state_of_I_O = tokenizer.nextToken().trim().toUpperCase();
					if ("I".equals(state_of_I_O) && record.bankId.equals(SORUSH_BIN) && "n".equals(state_of_n_nn)) {
						if (RunningMode.MAIN.equals(RUNNING_MODE)) {
							logger.error(String.format("Found record with state (I,n,%s) in sorush file [%s]", SORUSH_BIN, line));
						} else if (RunningMode.UI.equals(RUNNING_MODE)) {
							throw new SorushDisagreementException(String.format("Found record with state (I,n,%s) in sorush file [%s]", SORUSH_BIN, line));
						}
					}
					if ("I".equals(state_of_I_O) && record.bankId.equals(SORUSH_BIN)) {
						continue;
					}
					record.state_of_I_O = state_of_I_O;
					String sorushSeqCntr = tokenizer.nextToken();
					if ("I".equals(state_of_I_O) && !record.bankId.equals(SORUSH_BIN) && "nn".equals(state_of_n_nn) && !"0".equals(sorushSeqCntr)) {
						if (RunningMode.MAIN.equals(RUNNING_MODE)) {
							logger.error(String.format("Found record with state (I,nn,%s) with sorushSeqCntr : %s in sorush file [%s]", record.bankId, sorushSeqCntr != null ? sorushSeqCntr : "null", line));
						} else if (RunningMode.UI.equals(RUNNING_MODE)) {
							throw new SorushDisagreementException(String.format("Found record with state (I,nn,%s) with sorushSeqCntr : %s in sorush file [%s]", record.bankId, sorushSeqCntr != null ? sorushSeqCntr : "null", line));
						}
					}
					result.add(record);
				}
			}
			return result;
		} catch (SorushDisagreementException e) {
			if (RunningMode.MAIN.equals(RUNNING_MODE)) {
				logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
				return null;
			} else {
				logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
				throw e;
			}
		} catch (Exception e) {
			logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
			return null;
		} finally {
			br.close();
		}
	}

	private Vector<SorushRecord> getSorushRecord_from_file_sorushRes(final File sorushFile, final Information info, final Vector<String> v_error_tt_result) throws SorushDisagreementException, IOException {
		BufferedReader br =  new BufferedReader(new FileReader(sorushFile));
		PersianDateFormat dateFormatPers = new PersianDateFormat("yyyyMMdd");
		String line = "";
		SorushRecord record = null;
		Vector<SorushRecord> result = new Vector<SorushRecord>();
		StringTokenizer tokenizer;
		Long row = 0L;
		try {
			while (br.ready()) {
				if ((line = br.readLine()).length() > 0) {
					row++;
					logger.info("sorush" + row + ": " + line);
					record = new SorushRecord();
					record.data = line;
					tokenizer = new StringTokenizer(line, "|");

					record.persianDt =  "13" + tokenizer.nextToken().trim();
					Date date = dateFormatPers.parse(record.persianDt);
					record.trxDate = new DayDate(date);
					record.trnSeqCntr = tokenizer.nextToken().trim();
					if (record.trnSeqCntr != null && !record.trnSeqCntr.equals("null")) {
						record.trnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, record.trnSeqCntr, '0');
					}
					record.appPan = tokenizer.nextToken().trim();
					tokenizer.nextToken();
					record.bankId = Long.valueOf(tokenizer.nextToken().trim());

					//AldComment baraye inke tedadi az all errors ha ra kam konim barkhai az bin bank ha ra agar ba bin psp anha begardim hal mishavad 
					// in tekke code baraye in ejad shod ke yek tedadi az all errors ha ra kam konim


					for (int i = 0; i < 2; i++) {
						tokenizer.nextToken();
					}

					String state_of_n_nn = tokenizer.nextToken().trim().toLowerCase();
					String state_of_I_O = tokenizer.nextToken().trim().toUpperCase();
					if ("I".equals(state_of_I_O) && record.bankId.equals(SORUSH_BIN) && "n".equals(state_of_n_nn)) {
						if (RunningMode.MAIN.equals(RUNNING_MODE)) {
							logger.error(String.format("Found record with state (I,n,%s) in sorush file [%s]", SORUSH_BIN, line));
						} else if (RunningMode.UI.equals(RUNNING_MODE)) {
							throw new SorushDisagreementException(String.format("Found record with state (I,n,%s) in sorush file [%s]", SORUSH_BIN, line));
						}
					}
					if ("I".equals(state_of_I_O) && record.bankId.equals(SORUSH_BIN)) {
						continue;
					}
					record.state_of_I_O = state_of_I_O;
					String sorushSeqCntr =  tokenizer.nextToken();
					if ("I".equals(state_of_I_O) && !record.bankId.equals(SORUSH_BIN) && "nn".equals(state_of_n_nn) && !"0".equals(sorushSeqCntr)) {
						if (RunningMode.MAIN.equals(RUNNING_MODE)) {
							logger.error(String.format("Found record with state (I,nn,%s) with sorushSeqCntr : %s in sorush file [%s]", record.bankId, sorushSeqCntr != null ? sorushSeqCntr : "null", line));
						} else if (RunningMode.UI.equals(RUNNING_MODE)) {
							throw new SorushDisagreementException(String.format("Found record with state (I,nn,%s) with sorushSeqCntr : %s in sorush file [%s]", record.bankId, sorushSeqCntr != null ? sorushSeqCntr : "null", line));
						}
					}
					
					if (tokenizer.hasMoreTokens()) {
						record.amount = Long.valueOf(tokenizer.nextToken().trim());
						record.trxType = tokenizer.nextToken().trim();
						record.lifeCycleId = tokenizer.nextToken().trim();
						record.sorushFileDate = tokenizer.nextToken().trim();
						record.isChangeBin = Boolean.valueOf(tokenizer.nextToken().trim());
						if (record.isChangeBin) {
							changeBankId(record);
						}
						record.workingDay = tokenizer.nextToken().trim();
						
						if (tokenizer.hasMoreTokens()) {
							record.sorushPersianDt = tokenizer.nextToken().trim();
							record.sorushTrnSeqCntr = tokenizer.nextToken().trim();
							if (record.sorushTrnSeqCntr != null && !record.sorushTrnSeqCntr.equals("null")) {
								record.sorushTrnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, record.sorushTrnSeqCntr, '0');
							}
							record.sorushWorkingDay = tokenizer.nextToken().trim();

							String transactionType = tokenizer.nextToken().trim();
							Byte transactionTypeByte = transactionType.toLowerCase().equals("null") ? null : Byte.valueOf(transactionType);
							record.sorushTransactionType = new TransactionType(transactionTypeByte);
						}
					}
					if (!line.toUpperCase().contains(IncrementalTransfer)) {
						result.add(record);
					} else if (v_error_tt_result != null) {
						v_error_tt_result.add(line);
					}
				}
			}
			return result;
		} catch (SorushDisagreementException e) {
			if (RunningMode.MAIN.equals(RUNNING_MODE)) {
				logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
				return null;
			} else {
				logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
				throw e;
			}
		} catch (Exception e) {
			logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
			return null;
		} finally {
			br.close();
		}
	}


	private Vector<SorushRecord> getSorushRecord_from_buffer_sorush(final Vector<String> input, final Information info, final Boolean onlyForErrors_ChangeBins) throws SorushDisagreementException {
		PersianDateFormat dateFormatPers = new PersianDateFormat("yyyyMMdd");
		String line = "";
		SorushRecord record = null;
		Vector<SorushRecord> result = new Vector<SorushRecord>();
		StringTokenizer tokenizer;
		Long row = 0L;
		try {
			for (int rowIndex = 0; rowIndex < input.size(); rowIndex++) {
				line = input.get(rowIndex);
				row++;
				logger.info("sorush" + row + ": " + line);
				record = new SorushRecord();
				record.data = line;
				tokenizer = new StringTokenizer(line, "|");
				record.sorushFileDate = info.getDate();

				record.persianDt =  "13" + tokenizer.nextToken().trim();
				Date date = dateFormatPers.parse(record.persianDt);
				record.trxDate = new DayDate(date);
				record.trnSeqCntr = tokenizer.nextToken().trim();
				if (record.trnSeqCntr != null && !record.trnSeqCntr.equals("null")) {
					record.trnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, record.trnSeqCntr, '0');
				}
				record.appPan = tokenizer.nextToken().trim();
				tokenizer.nextToken();
				record.bankId = Long.valueOf(tokenizer.nextToken().trim());

				//AldComment baraye inke tedadi az all errors ha ra kam konim barkhai az bin bank ha ra agar ba bin psp anha begardim hal mishavad 
				// in tekke code baraye in ejad shod ke yek tedadi az all errors ha ra kam konim
				if (onlyForErrors_ChangeBins) {
					changeBankId(record);
				}

				for (int i = 0; i < 2; i++) {
					tokenizer.nextToken();
				}

				String state_of_n_nn = tokenizer.nextToken().trim().toLowerCase();
				String state_of_I_O = tokenizer.nextToken().trim().toUpperCase();
				if ("I".equals(state_of_I_O) && record.bankId.equals(SORUSH_BIN) && "n".equals(state_of_n_nn)) {
					if (RunningMode.MAIN.equals(RUNNING_MODE)) {
						logger.error(String.format("Found record with state (I,n,%s) in sorush file [%s]", SORUSH_BIN, line));
					} else if (RunningMode.UI.equals(RUNNING_MODE)) {
						throw new SorushDisagreementException(String.format("Found record with state (I,n,%s) in sorush file [%s]", SORUSH_BIN, line));
					}
				}
				if ("I".equals(state_of_I_O) && record.bankId.equals(SORUSH_BIN)) {
					continue;
				}
				record.state_of_I_O = state_of_I_O;
				String sorushSeqCntr =  tokenizer.nextToken();
				if ("I".equals(state_of_I_O) && !record.bankId.equals(SORUSH_BIN) && "nn".equals(state_of_n_nn) && !"0".equals(sorushSeqCntr)) {
					if (RunningMode.MAIN.equals(RUNNING_MODE)) {
						logger.error(String.format("Found record with state (I,nn,%s) with sorushSeqCntr : %s in sorush file [%s]", record.bankId, sorushSeqCntr != null ? sorushSeqCntr : "null", line));
					} else if (RunningMode.UI.equals(RUNNING_MODE)) {
						throw new SorushDisagreementException(String.format("Found record with state (I,nn,%s) with sorushSeqCntr : %s in sorush file [%s]", record.bankId, sorushSeqCntr != null ? sorushSeqCntr : "null", line));
					}
				}
				result.add(record);
			}
			return result;
		} catch (SorushDisagreementException e) {
			if (RunningMode.MAIN.equals(RUNNING_MODE)) {
				logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
				return null;
			} else {
				logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
				throw e;
			}
		} catch (Exception e) {
			logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
			return null;
		}
	}

	private Vector<SorushRecord> getSorushRecord_from_file_sorushResAfterSanad(final File sorushFile, final boolean isDisagreement, final Information info, final Vector<String> v_error_tt_result, final Boolean skipCheckDateInCompare) throws IOException {
		BufferedReader br =  new BufferedReader(new FileReader(sorushFile));
		PersianDateFormat dateFormatPers = new PersianDateFormat("yyyyMMdd");
		String line = "";
		SorushRecord record = null;
		Vector<SorushRecord> result = new Vector<SorushRecord>();
		StringTokenizer tokenizer;
		Long row = 0L;
		try {
			while (br.ready()) {
				if ((line = br.readLine()).length() > 0) {
					row++;
					logger.info("sorush" + row + ": " + line);
					record = new SorushRecord();
					tokenizer = new StringTokenizer(line, "|");

					//remove this
					//AldTODO Task080 : Yefekri baraye tarikhe file beshavad
//					if (!getDateFromFileName) {
//						record.sorushFileDate = tokenizer.nextToken().trim();
//					} else {
//						record.sorushFileDate = null;
//					}

					record.persianDt =  tokenizer.nextToken().trim();
					Date date = dateFormatPers.parse(record.persianDt);
					record.trxDate = new DayDate(date);
					record.trnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, tokenizer.nextToken().trim(), '0');
					if (record.trnSeqCntr != null && !record.trnSeqCntr.equals("null")) {
						record.trnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, record.trnSeqCntr, '0');
					}
					record.appPan = tokenizer.nextToken().trim();
					record.bankId = Long.valueOf(tokenizer.nextToken().trim());


					if (isDisagreement) {
						if (tokenizer.hasMoreTokens()) {
							record.workingDay = tokenizer.nextToken().trim();
							record.sorushPersianDt = tokenizer.nextToken().trim();
							record.sorushTrnSeqCntr = tokenizer.nextToken().trim();
							if (record.sorushTrnSeqCntr != null && !record.sorushTrnSeqCntr.equals("null")) {
								record.sorushTrnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, record.sorushTrnSeqCntr, '0');
							}
							record.sorushWorkingDay = tokenizer.nextToken().trim();
							record.trxType = tokenizer.nextToken().trim(); //me
							record.amount = Long.valueOf(tokenizer.nextToken().trim()); //me

							String nullString = "null";
							//Niaz hast check shavad
							if (record.sorushPersianDt.toLowerCase().equals(nullString)) {
								record.sorushPersianDt = null;
							}

							if (record.sorushTrnSeqCntr.toLowerCase().equals(nullString)) {
								record.sorushTrnSeqCntr = null;
							}

							if (record.sorushWorkingDay.toLowerCase().equals(nullString)) {
								record.sorushWorkingDay = null;
							}
							//

							record.lifeCycleId = tokenizer.nextToken().trim(); //TASK Task103
							record.sorushFileDate = tokenizer.nextToken().trim();
							tokenizer.nextToken(); //skipCheckDate
							//AldComment baraye inke tedadi az all errors ha ra kam konim barkhai az bin bank ha ra agar ba bin psp anha begardim hal mishavad 
							// in tekke code baraye in ejad shod ke yek tedadi az all errors ha ra kam konim
							record.isChangeBin = Boolean.valueOf(tokenizer.nextToken().trim());
							if (record.isChangeBin) {
								changeBankId(record);
							}

							record.documentNumber = tokenizer.nextToken().trim();
							record.documentPattern = tokenizer.nextToken().trim();


							if (!line.toUpperCase().contains(IncrementalTransfer)) {
								if ((record.documentNumber == null || record.documentNumber.toLowerCase().equals("null") && !record.trxType.toUpperCase().equals(BalanceInquery))) {  //AldTODO Balance ???
									if (!skipCheckDateInCompare) {
										result.add(record);
									} else {
										if (record.documentPattern.startsWith("notin8sh_notin8b")) {
											result.add(record);
										} else {
											logger.info(String.format("SKIP - not is notin8sh_in8b[_tf] record [%s] ", record.toString()));
										}
									}
								} else {
									logger.info(String.format("SKIP - Sandad before [%s]", record.toString()));
								}
							} else {
								v_error_tt_result.add(line);
							}


						}
					} else {
						record.data = line;
//						result.add(record); //comment in 92.12.21
						if (!line.toUpperCase().contains(IncrementalTransfer)) {
							result.add(record);
						} else {
							v_error_tt_result.add(line);
						}

					}
				}
			}
			return result;
		} catch (RuntimeException e) {
			if (RunningMode.MAIN.equals(RUNNING_MODE)) {
				logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
				return null;
			} else {
				logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
				throw e;
			}
		} catch (Exception e) {
			logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
			return null;
		} finally {
			br.close();
		}
	}

	private Vector<SorushRecord> getSorushRecord_from_buffer_sorushResAfterSanad(final Vector<String> input, final boolean isDisagreement, final Information info, final Vector<String> v_error_tt_result, final Boolean skipCheckDateInCompare) {
		PersianDateFormat dateFormatPers = new PersianDateFormat("yyyyMMdd");
		String line = "";
		SorushRecord record = null;
		Vector<SorushRecord> result = new Vector<SorushRecord>();
		StringTokenizer tokenizer;
		Long row = 0L;

		try {
			for (int i = 0; i < input.size(); i++) {
				line = input.get(i);
				row++;
				logger.info("sorush" + row + ": " + line);
				record = new SorushRecord();
				tokenizer = new StringTokenizer(line, "|");
				record.persianDt =  tokenizer.nextToken().trim();
				Date date = dateFormatPers.parse(record.persianDt);
				record.trxDate = new DayDate(date);
				record.trnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, tokenizer.nextToken().trim(), '0');
				if (record.trnSeqCntr != null && !record.trnSeqCntr.equals("null")) {
					record.trnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, record.trnSeqCntr, '0');
				}
				record.appPan = tokenizer.nextToken().trim();
				record.bankId = Long.valueOf(tokenizer.nextToken().trim());
				if (isDisagreement) {
					if (tokenizer.hasMoreTokens()) {
						record.workingDay = tokenizer.nextToken().trim();
						record.sorushPersianDt = tokenizer.nextToken().trim();
						record.sorushTrnSeqCntr = tokenizer.nextToken().trim();
						if (record.sorushTrnSeqCntr != null && !record.sorushTrnSeqCntr.equals("null")) {
							record.sorushTrnSeqCntr = StringFormat.formatNew(6, StringFormat.JUST_RIGHT, record.sorushTrnSeqCntr, '0');
						}
						record.sorushWorkingDay = tokenizer.nextToken().trim();
						record.trxType = tokenizer.nextToken().trim(); //me
						record.amount = Long.valueOf(tokenizer.nextToken().trim()); //me

						String nullString = "null";
						//Niaz hast check shavad
						if (record.sorushPersianDt.toLowerCase().equals(nullString)) {
							record.sorushPersianDt = null;
						}

						if (record.sorushTrnSeqCntr.toLowerCase().equals(nullString)) {
							record.sorushTrnSeqCntr = null;
						}

						if (record.sorushWorkingDay.toLowerCase().equals(nullString)) {
							record.sorushWorkingDay = null;
						}

						record.lifeCycleId = tokenizer.nextToken().trim(); //TASK Task103
						record.sorushFileDate = tokenizer.nextToken().trim();
						tokenizer.nextToken(); //skipCheckDate
						//AldComment baraye inke tedadi az all errors ha ra kam konim barkhai az bin bank ha ra agar ba bin psp anha begardim hal mishavad 
						// in tekke code baraye in ejad shod ke yek tedadi az all errors ha ra kam konim
						record.isChangeBin = Boolean.valueOf(tokenizer.nextToken().trim());
						if (record.isChangeBin) {
							changeBankId(record);
						}
						record.documentNumber = tokenizer.nextToken().trim();
						record.documentPattern = tokenizer.nextToken().trim();


						if (!line.toUpperCase().contains(IncrementalTransfer)) {
							if ((record.documentNumber == null || record.documentNumber.toLowerCase().equals("null") && !record.trxType.toUpperCase().equals(BalanceInquery))) { //AldTODO Balance ??? 
								if (!skipCheckDateInCompare) {
									result.add(record);
								} else {
									if (record.documentPattern.startsWith("notin8sh_notin8b")) {
										result.add(record);
									} else {
										logger.info(String.format("SKIP - not is notin8sh_in8b[_tf] record [%s] ", record.toString()));
									}
								}
							} else {
								logger.info(String.format("SKIP - Sandad before [%s]", record.toString()));
							}
						} else {
							v_error_tt_result.add(line);
						}


					}
				} else {
					record.data = line;
//					result.add(record); //comment in 92.12.21
					if (!line.toUpperCase().contains(IncrementalTransfer)) {
						result.add(record);
					} else {
						v_error_tt_result.add(line);
					}
			}
			}
			return result;
		} catch (RuntimeException e) {
			if (RunningMode.MAIN.equals(RUNNING_MODE)) {
				logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
				return null;
			} else {
				logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
				throw e;
			}
		} catch (Exception e) {
			logger.error(String.format("error in extracting sorush record (%s)", info.getSoroshFileName()));
			return null;
		}

	}

	private void changeBankId(final SorushRecord record) {
		if (record.bankId.equals(622106L)) {
			record.bankId = 581672061L;
		}

		if (record.bankId.equals(603799L)) {
			record.bankId = 581672131L;
		}

		if (record.bankId.equals(621986L)) {
			record.bankId = 581672041L;
		}

		if (record.bankId.equals(627353L)) {
			record.bankId = 581672111L;
		}

		if (record.bankId.equals(627412L)) {
			record.bankId = 581672052L;
		}

		if (record.bankId.equals(603769L)) {
			record.bankId = 581672111L;
		}

		if (record.bankId.equals(603770L)) {
			record.bankId = 581672091L;
		}

		if (record.bankId.equals(627381L)) {
			record.bankId = 581672091L;
		}

		//code 2 raghami pspha dar form 8 moadele code shaparak mibashad
		if (record.bankId.toString().startsWith(SHAPARAK_PRE_BIN)) {
			record.bankId = 581672000L;
		}
	}

	private List<SorushRecordCompact> getShetabRecordForSorush(final Integer threadId, final BufferedReader br, final String dummy, final List<String> validList) {
		String line = "";
		SorushRecordCompact record = null;
		SorushRecordCompact tempRecord = new SorushRecordCompact();

		List<SorushRecordCompact> result = new ArrayList<SorushRecordCompact>();
		StringTokenizer tokenizer;
		Long row = 0L;
		try {
			while (br.ready()) {
				if ((line = br.readLine()).length() > 0) {
					row++;
					if (row % 5000 == 0) {
						 //logger.debug("Thread [" + threadId + "]" + " shetab row: "++  row);
						 logger.info("Thread [" + threadId + "]" + " shetab row: " +  row);
					}
					//tempRecord = new SorushRecordCompact_v2();
					tempRecord.clear();

					tokenizer = new StringTokenizer(line, "|");
					tokenizer.nextToken();
					tokenizer.nextToken();
					tempRecord.persianDt = tokenizer.nextToken().replaceAll("/", "").trim();
					tokenizer.nextToken();
					tempRecord.trnSeqCntr = tokenizer.nextToken().trim();
					tempRecord.appPan = tokenizer.nextToken().trim();
					String bankId = tokenizer.nextToken().trim();
					if (bankId.startsWith(SHAPARAK_PRE_BIN)) {
						bankId = SHAPARAK_BIN;
					}
					tempRecord.bankId = Long.valueOf(bankId);

				}
				if (validList.indexOf(tempRecord.appPan) != -1) {
					record = new SorushRecordCompact();
					record.appPan = tempRecord.appPan;
					record.bankId = tempRecord.bankId;
					record.persianDt = tempRecord.persianDt;
					record.trnSeqCntr = tempRecord.trnSeqCntr;
					result.add(record);
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("Exception in thread [" + threadId + "]  Shetab , " + row + " : " + e.getMessage() + " - " + line + " - " + dummy);
			return null;
		}
	}

	private List<SorushRecordCompact> getShetabRecordForSorush_inq(final Integer threadId, final BufferedReader br, final String dummy, final List<String> validList) {
		String line = "";
		SorushRecordCompact record = null;
		SorushRecordCompact tempRecord = new SorushRecordCompact();

		List<SorushRecordCompact> result = new ArrayList<SorushRecordCompact>();
		StringTokenizer tokenizer;
		Long row = 0L;
		try {
			while (br.ready()) {
				if ((line = br.readLine()).length() > 0) {
					row++;
					if (row % 5000 == 0) {
						 //logger.debug("Thread [" + threadId + "]" + " shetab row: "++  row);
						 logger.info("Thread [" + threadId + "]" + " shetab row: " + row);
					}
					//tempRecord = new SorushRecordCompact_v2();
					tempRecord.clear();

					tokenizer = new StringTokenizer(line, "|");
					tokenizer.nextToken();
					tempRecord.persianDt = tokenizer.nextToken().replaceAll("/", "").trim();
					tokenizer.nextToken();
					tempRecord.appPan = tokenizer.nextToken().trim();
					String bankId = tokenizer.nextToken().trim();
					if (bankId.startsWith(SHAPARAK_PRE_BIN)) {
						bankId = SHAPARAK_BIN;
					}
					tempRecord.bankId = Long.valueOf(bankId);
					tokenizer.nextToken();
					tempRecord.trnSeqCntr = tokenizer.nextToken().trim();

				}
				if (validList.indexOf(tempRecord.appPan) != -1) {
					record = new SorushRecordCompact();
					record.appPan = tempRecord.appPan;
					record.bankId = tempRecord.bankId;
					record.persianDt = tempRecord.persianDt;
					record.trnSeqCntr = tempRecord.trnSeqCntr;
					result.add(record);
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("Exception in thread [" + threadId + "]  Shetab , " + row + " : " + e.getMessage() + " - " + line + " - " + dummy);
			return null;
		}
	}

	public final List<SorushRecordCompact> getBankRecordForSorush(final Integer threadId, final BufferedReader br, final String dummy, final List<String> validList) {
		String line = "";
		SorushRecordCompact tempRecord =  new SorushRecordCompact();
		SorushRecordCompact record = null;
		List<SorushRecordCompact> result = new ArrayList<SorushRecordCompact>();
		StringTokenizer tokenizer;
		Long row = 0L;
		try {
			while (br.ready()) {
				if ((line = br.readLine()).length() > 0) {

					row++;
					if (row % 5000 == 0) {
						//logger.debug("Thread [" + threadId +  "] " + "bank row: " + row);
						logger.info("Thread [" + threadId + "] " + "bank row: " + row);
					}
					tempRecord.clear();

					tokenizer = new StringTokenizer(line, "/");
					tempRecord.persianDt = "13" + tokenizer.nextToken().trim().substring(0, 6);


					for (int i = 0; i < 12; i++) {
						tokenizer.nextToken();
					}
					tempRecord.trnSeqCntr = tokenizer.nextToken().trim();
					tokenizer.nextToken();
					Integer bankCode = Integer.valueOf(tokenizer.nextToken().trim());



					tokenizer.nextToken();
					String appPan = tokenizer.nextToken().trim();

					if (appPan.contains(BANK_BIN)) {
						tempRecord.appPan = appPan;
					} else {
						appPan = tokenizer.nextToken().trim();
						if (appPan.contains(BANK_BIN)) {
							tempRecord.appPan = appPan;
						} else {
							tempRecord.appPan = tokenizer.nextToken().trim();
						}
					}

					//AldComment Baraye Halle Moshkele Ghavamin & Saman
					Long persianDtLong = Long.valueOf(tempRecord.persianDt);
					Long dateL = 13920231L; //Tarikhi Taghirat Jadid Dar Bank Emal Shade
					Long dateU = 13920307L; //Tarikhi ke Taghirat ra switch fahmide
					if (persianDtLong < dateL) {
						if (bankCode.equals(0)) {
							logger.error(String.format("Error in bank Code in Line - %s in file - %s", line, dummy));
						}
						if (bankCode.equals(52)) {
							bankCode = 56; //BankCode Saman
							//logger.debug(String.format("Date is less than 13920231 convert BankCode 52->56 - " + line));
						}
					}
					if (persianDtLong >= dateL && persianDtLong < dateU) {
						if (bankCode.equals(0)) {
							bankCode = 52; //BanCode Ghavamin
							//logger.debug(String.format("Date is between 13920231 and 13920307 convert BankCode 0->52 - " + line ));
						}
						if (bankCode.equals(52)) {
							bankCode = 56;
							//logger.debug(String.format("Date is between 13920231 and 13920307 convert BankCode 52->56 - " + line));
						}
					}


//					record.bankId = getBinByTwoDigit(bankCode);
					tempRecord.bankId = GetBinByTwoDigit.get(bankCode);

				}
				if (validList.indexOf(tempRecord.appPan) != -1) {
					record = new SorushRecordCompact();
					record.appPan = tempRecord.appPan;
					record.bankId = tempRecord.bankId;
					record.persianDt = tempRecord.persianDt;
					record.trnSeqCntr = tempRecord.trnSeqCntr;
					result.add(record);
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("Exception in thread [" + threadId + "]  Bank , " + row + " : " +  e.getMessage() + " - " + line + " - " + dummy);
			return result;
		}
	}

	private String sanad(final List<DocumentItemEntity> docs, final SorushRecord sorushRecord, final String description, final String date) {
		return sanad(docs, sorushRecord, description, date, "ASNADE_TAKMILIE_SOROSH_");
	}

	private String sanad(final List<DocumentItemEntity> docs , final SorushRecord sorushRecord, final String description, final String date, final String preBillId) {
		try {
			Pair<String, String> document = AccountingService.generateFCBDocument(description + " مورخ " +  date
					, docs, null, preBillId + sorushRecord.appPan + "- " +  sorushRecord.trnSeqCntr + "-" + sorushRecord.workingDay , null, null, null);
			logger.info(document.first);
			SettlementReport report = new SettlementReport(Core.FANAP_CORE, document.first, document.second, null);

			if (sorushRecord.amount.equals(0L)) {
				logger.info(String.format("amount is zero , SKIP sanad for apppan : %s , trnSeqCntr : %s , workingDay : %s", sorushRecord.appPan, sorushRecord.trnSeqCntr, sorushRecord.workingDay));
				return "null";
			}

			//AldComment Agar doSanad false bashad marhalye sanad ra anjam nemidahad
			if (!doSanad) {
				return "-1";
			}


			String transactionId = AccountingService.issueFCBDocument(report, false);

			//try for sanad 
			int tryCounter = 1;
			while (!Util.hasText(transactionId) && tryCounter <= MAXIMUM_TRY_FOR_SANAD) {
				logger.info(String.format("try sanad for %s n", tryCounter));
				try {
					Thread.sleep(10);
					transactionId = AccountingService.issueFCBDocument(report, false);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					logger.error(e);
				}
				tryCounter++;
			}

			logger.info(transactionId);
			logger.debug(transactionId);
			return transactionId;
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static Long getBinByTwoDigit(final Integer twoDigitCode) {
		Map<Integer, Bank> banks = GlobalContext.getInstance().getAllBanks();
		return Long.valueOf(banks.get(twoDigitCode).getBin());
	}

	private static void extractFile(final ZipInputStream in, final File outdir, final String name) throws IOException
	  {
	    byte[] buffer = new byte[1024];
	    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outdir, name)));
	    int count = -1;
	    while ((count = in.read(buffer)) != -1) {
			out.write(buffer, 0, count);
		}
	    out.close();
	  }

	private static void mkdirs(final File outdir, final String path) {
	  File d = new File(outdir, path);
	  if (!d.exists()) {
		d.mkdirs();
	  }
	}

	private static String dirpart(final String name) {
	  int s = name.lastIndexOf(File.separatorChar);
	  return s == -1 ? null : name.substring(0, s);
	}

	public static boolean isZip(final File file) throws IOException {
	      if (file.isDirectory()) {
	          return false;
	      }
	      if (!file.canRead()) {
	          throw new IOException("Cannot read file " + file.getAbsolutePath());
	      }
	      if (file.length() < 4) {
	          return false;
	      }
	      DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
	      int test = in.readInt();
	      in.close();
	      return test == 0x504b0304;
	  }

    private static void saveReport(final Vector<String> lst, final String destinationPath, final String destinationFileName) throws IOException {
        // create report date
        StringBuilder report = new StringBuilder("");
        for (String trxId : lst) {
            report.append(String.format(trxId + "\r\n"));
        }
        // --------- Save Report
        File reportPath = new File(destinationPath);
        reportPath.mkdirs();
        if (!reportPath.exists()) {
			reportPath.createNewFile();
		}

        File fReport = new File(String.format(reportPath + "/%s", destinationFileName));
        if (fReport.exists()) {
            logger.debug(String.format("File %s alreadey exist.replaced it !!!", fReport.getAbsoluteFile()));
            if (fReport.delete()) {
                logger.debug(String.format("file deleted succseefully !!!"));
            } else {
                logger.error(String.format("An error occure in deleting file %s", fReport));
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(fReport);
            fos.write(report.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            logger.error(e);
        }
    }

	class SanadThread implements Runnable {
		Logger logger = Logger.getLogger(this.getClass());
		SorushDisagreementParallelWithSanadPasargad_ver2 owner;
		Integer threadId;
		Information info;
		Boolean skipCheckDateInCompare;

		List<SorushRecord> issueJobs;

		Vector<String> sanad_result;
		Vector<String> error_result;
		Vector<String> other_result;
		Vector<String> sanad_error_result;

		public SanadThread(final SorushDisagreementParallelWithSanadPasargad_ver2 owner, final Information info, final Boolean skipCheckDateInCompare, final List<SorushRecord> sorushRecords, final Vector<String> sanad_result, final Vector<String> error_result, final Vector<String> other_result, final Vector<String> sanad_error_result) {
			super();
			this.owner = owner;
			this.info = info;
			this.skipCheckDateInCompare =  skipCheckDateInCompare;
			this.threadId = info.getThreadId();
			this.issueJobs = sorushRecords;
			this.sanad_result = sanad_result;
			this.error_result = error_result;
			this.other_result = other_result;
			this.sanad_error_result = sanad_error_result;
		}


		@Override
		public void run() {
			logger.debug("I am here... " + threadId);
			try {
				owner.processSoroushRecords(info, skipCheckDateInCompare, sanad_result, error_result, other_result, sanad_error_result, issueJobs);
			} catch (IOException e) {
				e.printStackTrace();
			}

			logger.debug("I am exiting.... " + threadId);
		}
	}

	class ReadFormBankShatabThread implements Runnable {
		Logger logger = Logger.getLogger(this.getClass());
		SorushDisagreementParallelWithSanadPasargad_ver2 owner;
		Integer threadId;
		Vector<SorushRecord> issueJobs;
		List<String> workingDays;
		Map<String, BufferedReader> map;
		Boolean skipCheckDateInCompare;

		public ReadFormBankShatabThread(final SorushDisagreementParallelWithSanadPasargad_ver2 owner, final Integer id , final List<String> workingDays, final Map<String, BufferedReader> map, final Vector<SorushRecord> issueJobs, final Boolean skipCheckDateInCompare) {
			super();
			this.owner = owner;
			this.threadId = id;
			this.issueJobs = issueJobs;
			this.workingDays = workingDays;
			this.map = map;
			this.skipCheckDateInCompare = skipCheckDateInCompare;
		}


		@Override
		public void run() {
			logger.debug("I am here... " + threadId);
			owner.readFormsToCompare(threadId, map, issueJobs, workingDays, skipCheckDateInCompare);
			logger.debug("I am exiting.... " + threadId);
		}
	}

	class ReadFormBankShatabSorushThread implements Runnable {
		Logger logger = Logger.getLogger(this.getClass());
		SorushDisagreementParallelWithSanadPasargad_ver2 owner;
		Integer threadId;
		Vector<SorushRecord> issueJobs;
		List<String> workingDays;
		Map<String, BufferedReader> map;
		Boolean skipCheckDateInCompare;

		public ReadFormBankShatabSorushThread(final SorushDisagreementParallelWithSanadPasargad_ver2 owner, final Integer id, final List<String> workingDays, final Map<String, BufferedReader> map, final Vector<SorushRecord> issueJobs, final Boolean skipCheckDateInCompare) {
			super();
			this.owner = owner;
			this.threadId = id;
			this.issueJobs = issueJobs;
			this.workingDays = workingDays;
			this.map = map;
			this.skipCheckDateInCompare = skipCheckDateInCompare;
		}


		@Override
		public void run() {
			logger.debug("I am here... " + threadId);
			owner.readFormsToCompareForSoroush(threadId, map, issueJobs, workingDays, skipCheckDateInCompare);
			logger.debug("I am exiting.... " + threadId);
		}
	}

	class Information {
		private Integer threadId;
		private String soroshFileName;
		private String date;
		public Information(final String soroshFileName) {
			this.soroshFileName = soroshFileName;
		}

		public String getSoroshFileName() {
			if (Util.hasText(soroshFileName)) {
				return soroshFileName;
			} else {
				return "";
			}
		}

		public Integer getThreadId() {
			return threadId;
		}

		public void setThreadId(final Integer threadId) {
			this.threadId = threadId;
		}

		public String getDate() {
			return date;
		}

		public void setDate(final String date) {
			this.date = date;
		}
	}

	class SorushRecordCompact {
		public String persianDt;
		public String trnSeqCntr;
		public String appPan;
		public Long bankId;

		public void clear() {
			this.appPan = null;
			this.bankId = null;
			this.persianDt = null;
			this.trnSeqCntr = null;
		}
	}

}