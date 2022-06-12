package com.michaeldmiller.economicagents;

public class JobOutput {
    private String job;
    private String good;

    public JobOutput(String job, String good) {
        this.job = job;
        this.good = good;
    }

    public String getJob() {
        return job;
    }

    public String getGood() {
        return good;
    }

    public void setJob(String newJob) {
        job = newJob;
    }

    public void setGood(String newGood) {
        good = newGood;
    }

    public String toString() {
        return ("\n" + this.getJob() + " -> " + this.getGood());
    }
}
