package utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;

import MarkAny.MaSaferJavaUpgrade.Madec;
import MarkAny.MaSaferJavaUpgrade.Madn;
import excel.RedmineExcel;

/**
 * 하이마트 DRM 유틸
 * 
 * @author narusas
 *
 */
@Slf4j
public class HMDrmUtil {

    @Getter @Setter static String newDatFile;

    @Getter @Setter static String oldDatFile;


    public static void main(String[] args) {

    }


    public static File decode(File encryptedSourceFile) {

        try {
            File returnFile = File.createTempFile("drmfile", "." + FilenameUtils.getExtension(encryptedSourceFile.getName()));
            System.out.println("Temp file: {}" + returnFile.getAbsolutePath());
            decode(encryptedSourceFile, returnFile);
            return returnFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void decode(File encryptedSourceFile, File targetFile) {

        System.out.println("encryptedSourceFile ::" + encryptedSourceFile.getAbsolutePath());
        System.out.println("targetFile ::" + targetFile.getAbsolutePath());
        if (encryptedSourceFile.length() == 0) {
            throw new RuntimeException("ERR streamDec_FileSample length is zero.");
        }
        String drmPath1 = "C:\\work\\workspace\\DeployManager\\src\\MarkAnyDrmInfo51014_common.dat";
        String drmPath2 = "C:\\work\\workspace\\DeployManager\\src\\MarkAnyDrmInfo_common.dat";
        // String oldPath = new ClassPathResource(drmPath1).getFile().getAbsolutePath();
        // String newPath = new ClassPathResource(drmPath2).getFile().getAbsolutePath();
        // log.debug("HMDrmUtil Config location Old:{} New: {}", oldPath, newPath);

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(encryptedSourceFile));
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile));) {
            Madec clMadec = new Madec(drmPath1, drmPath2);
            long lFileLen = encryptedSourceFile.length();
            System.out.println("lFileLen  ::" + lFileLen);
            long OutFileLength = clMadec.lGetDecryptFileSize(targetFile.getName(), lFileLen, in);

            System.out.println("DBG OutFileLength = {}" + OutFileLength);
            System.out.println("DBG return code = {}" + clMadec.strMadec(out));
            if (OutFileLength <= 0) {
                String strErrorCode = clMadec.strGetErrorCode();
                System.out.println("ERR [ErrorCode] {}  [ErrorDescription] {}" + strErrorCode + clMadec.strGetErrorMessage(strErrorCode));
                throw new RuntimeException("ERR [ErrorCode] " + strErrorCode + "[ErrorDescription] "
                        + clMadec.strGetErrorMessage(strErrorCode));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static File encode(File plainSourceFile) {

        try {
            File returnFile = File.createTempFile("drmfile", "." + FilenameUtils.getExtension(plainSourceFile.getName()));
            log.debug("Temp file: {}", returnFile.getAbsolutePath());
            encode(plainSourceFile, returnFile);
            return returnFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void encode(File plainSourceFile, File targetFile) {

        if (plainSourceFile.length() == 0) {
            throw new RuntimeException("ERR streamDec_FileSample length is zero.");
        }

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(plainSourceFile));
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile));) {

            long OutFileLength = 0;
            RedmineExcel redmineExcel = new RedmineExcel();
            // Madn clMadn = new Madn(redmineExcel.getOldDatFile(), redmineExcel.getNewDatFile());
            Madn clMadn = new Madn(oldDatFile, newDatFile);

            int piAclFlag = 0;
            // int piDocLevel = 1;
            String pstrDocLevel = "0";
            String pstrUserId = "dgbid1";
            String pstrFileName = "1.xls";
            long plFileSize = plainSourceFile.length();
            String pstrOwnerId = "pstrOwnerId";
            String pstrCompanyId = "HIMART-9Ad7-25EE-654A";
            String strNewCompanyId = "HIMART-6EE6-8928-BFC3";
            String pstrGroupId = "pstrGroupId";
            String pstrPositionId = "pstrPositionId";
            String pstrGrade = "pstrGrade";
            String pstrFileId = "20100804165120000";
            int piCanSave = 1;
            int piCanEdit = 1;
            int piBlockCopy = 1;
            int piOpenCount = -99;
            int piPrintCount = -99;
            int piValidPeriod = -99;
            int piSaveLog = 1;
            int piPrintLog = 1;
            int piOpenLog = 1;
            int piVisualPrint = 1;
            int piImageSafer = 1;
            int piRealTimeAcl = 0;
            String pstrDocumentTitle = "";
            String pstrCompanyName = "HIMART";
            String pstrGroupName = "pstrGroupName";
            String pstrPositionName = "pstrPositionName";
            String pstrUserName = "drm008";
            String pstrUserIp = "10.154.109.8";
            String pstrServerOrigin = "serverorigin";
            int piExchangePolicy = 1;
            int piDrmFlag = 0;
            int iBlockSize = 0;
            String strMachineKey = "";

            String strFileVersion = "";
            String strMultiUserID = "dgbid;dgbid2;";
            String strMultiUserName = "strSecurityLevelName";
            String strEnterpriseID = "HIMARTG-6079-07B9-1D5E";
            String strEnterpriseName = "";
            String strDeptID = "deptid";
            String strDeptName = "deptname";
            String strPositionLevel = "";
            // String strSecurityLevel = "EDM00");
            // String strSecurityLevel = "EDM10");
            String strSecurityLevel = "1";
            String strSecurityLevelName = "";
            String strPgCode = "";
            String strCipherBlockSize = "16";
            String strCreatorID = "";
            String strCreatorName = "";
            String strOnlineContorl = "0";
            String strOfflinePolicy = "";
            String strValidPeriodType = "";
            String strUsableAlways = "0";
            String strPriPubKey = "";

            String strCreatorCompanyId = "mark1";
            String strCreatorDeptId = "mark2";
            String strCreatorGroupId = "mark3";
            String strCreatorPositionId = "mark4";
            String strFileSize = "4567";
            String strHeaderUpdateTime = "";
            String strReserved01 = "reserved01";
            String strReserved02 = "reserved02";
            String strReserved03 = "reserved03";
            String strReserved04 = "reserved04";
            String strReserved05 = "reserved05";

            OutFileLength =
                    clMadn.lGetEncryptFileSize(piAclFlag, pstrDocLevel, pstrUserId, pstrFileName, plFileSize, pstrOwnerId, pstrCompanyId,
                            pstrGroupId, pstrPositionId, pstrGrade, pstrFileId, piCanSave, piCanEdit, piBlockCopy, piOpenCount,
                            piPrintCount, piValidPeriod, piSaveLog, piPrintLog, piOpenLog, piVisualPrint, piImageSafer, piRealTimeAcl,
                            pstrDocumentTitle, pstrCompanyName, pstrGroupName, pstrPositionName, pstrUserName, pstrUserIp,
                            pstrServerOrigin, piExchangePolicy, piDrmFlag, iBlockSize, strMachineKey,

                            strFileVersion, strMultiUserID, strMultiUserName, strEnterpriseID, strEnterpriseName, strDeptID, strDeptName,
                            strPositionLevel, strSecurityLevel, strSecurityLevelName, strPgCode, strCipherBlockSize, strCreatorID,
                            strCreatorName, strOnlineContorl, strOfflinePolicy, strValidPeriodType, strUsableAlways, strPriPubKey,
                            strCreatorCompanyId, strCreatorDeptId, strCreatorGroupId, strCreatorPositionId, strFileSize,
                            strHeaderUpdateTime, strReserved01, strReserved02, strReserved03, strReserved04, strReserved05,

                            in,
                            // MarkAny.MaSaferJavaUpgrade.Madn.iMaDrmVersion_V25_51014,
                            MarkAny.MaSaferJavaUpgrade.Madn.iMaDrmVersion_V45, strNewCompanyId);
            log.debug("DBG return code = {}", clMadn.strMadn(out));
            if (OutFileLength <= 0) {
                String strErrorCode = clMadn.strGetErrorCode();
                log.warn("ERR [ErrorCode] {}  [ErrorDescription] {}", strErrorCode, clMadn.strGetErrorMessage(strErrorCode));
                throw new RuntimeException("ERR [ErrorCode] " + strErrorCode + "[ErrorDescription] "
                        + clMadn.strGetErrorMessage(strErrorCode));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static File encode(String fileName, File plainSourceFile) {

        try {
            File returnFile = File.createTempFile(fileName, "." + FilenameUtils.getExtension(plainSourceFile.getName()));
            log.debug("Temp file: {}", returnFile.getAbsolutePath());
            encode(plainSourceFile, returnFile);
            return returnFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // public static File toExcelFile(List<?> dataList, String[][] mapping, String title) throws IOException {
    //
    // File targetFile = null;
    // if (ExcelUtil.MAXIMUM_ROW_CNTS_PER_ONE_SHEET > dataList.size()) {
    // targetFile = File.createTempFile(title, ".xls");
    // ExcelUtil.toExcelFile(dataList, mapping, title, targetFile);
    // } else {
    // targetFile = File.createTempFile(title, ".xlsx");
    // ExcelUtil.toMultipleSheetsExcelFile(dataList, mapping, title, targetFile);
    // }
    //
    // return encode(title, targetFile);
    // }
    //
    //
    // public static File toGrpExcelFile(List<?> dataList, String[][] mapping, String title, File targetFile, List grp) {
    //
    // ExcelUtil.toGrpExcelFile(dataList, mapping, title, targetFile, grp);
    //
    // return encode(title, targetFile);
    // }
    //
    //
    // public static File toGrpMergeExcelFile(List<?> dataList, String[][] mapping, String title, File targetFile, List<String[]> grp) {
    //
    // ExcelUtil.toGrpMergeExcelFile(dataList, mapping, title, targetFile, grp);
    //
    // return encode(title, targetFile);
    // }
}
