package branchmerge.thread;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import branchmerge.model.WorkingResource;
import branchmerge.svn.Svn;

public class MergeResourceHandler implements Runnable {

    private final List resources;
    private String fromBranch;
    private String toBranch;
    private static int row;
    private List threads;
    ArrayList report;
    public ExcelReportHandler excelHandler;


    public MergeResourceHandler(List resources, String fromBranch, String toBranch, Svn svn, int row) {

        threads = new ArrayList();
        report = null;
        excelHandler = null;
        this.resources = resources;
        this.fromBranch = fromBranch;
        this.toBranch = toBranch;
        row = row;
        if (fromBranch.equals(toBranch)) {
            throw new IllegalArgumentException(
                    "\uAC19\uC740 \uBE0C\uB79C\uCE58\uB85C\uB294 \uC791\uC5C5\uD560\uC218 \uC5C6\uC2B5\uB2C8\uB2E4. ");
        } else {
            return;
        }
    }


    public MergeResourceHandler(List resources, String fromBranch, String toBranch, Svn svn, int row, ExcelReportHandler excelHandler) {

        this(resources, fromBranch, toBranch, svn, row);
        this.excelHandler = excelHandler;
    }


    public MergeResourceHandler(List list) {

        threads = new ArrayList();
        report = null;
        excelHandler = null;
        resources = list;
    }


    public MergeResourceHandler(List list, int row, ExcelReportHandler excelHandler) {

        this(list);
        this.excelHandler = excelHandler;
        row = row;
    }


    public MergeResourceHandler(List list, ArrayList report) {

        this(list);
        this.report = report;
    }


    public MergeResourceHandler(List list, ArrayList report, String toBranch) {

        this(list);
        this.report = report;
        this.toBranch = toBranch;
    }


    public MergeResourceHandler(List list, ArrayList report, String toBranch, String fromBranch) {

        this(list);
        this.report = report;
        this.toBranch = toBranch;
        this.fromBranch = fromBranch;
    }


    @Override
    public void run() {

        // System.out.println("fromBranch ::" + fromBranch);
        if (StringUtils.isNotEmpty(fromBranch)) {
            MergeResourceProcess(resources, toBranch, fromBranch);
        } else {
            MergeResourceProcess(resources, toBranch);
        }

        while (isThreadsRuning()) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void MergeResourceProcess(List rs, String toBranch) {

        for (int i = 0; i < rs.size(); i++) {
            Thread thread = new Thread(new MergeResource((WorkingResource) rs.get(i), report, toBranch));
            thread.start();
            threads.add(thread);
        }

    }


    private void MergeResourceProcess(List rs, String toBranch, String fromBranch) {

        for (int i = 0; i < rs.size(); i++) {
            Thread thread = new Thread(new MergeResource((WorkingResource) rs.get(i), report, toBranch, fromBranch));
            thread.start();
            threads.add(thread);
        }

    }


    private boolean isThreadsRuning() {

        for (Iterator iterator = threads.iterator(); iterator.hasNext();) {
            Thread tr = (Thread) iterator.next();
            if (tr.isAlive()) {
                return true;
            }
        }

        return false;
    }

}
