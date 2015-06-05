package com.tamr.field.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.integration.annotation.Transformer;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.messaging.Message;

import java.io.File;


public class JobRequstTransformer {

    private Job job;
    private String fileParameterName;

    public Job getJob() {
        return job;
    }

    public String getFileParameterName() {
        return fileParameterName;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void setFileParameterName(String fileParameterName) {
        this.fileParameterName = fileParameterName;
    }

    @Transformer
    public JobLaunchRequest toRequest(Message<File> message){

        JobParametersBuilder jobParametersBuilder =
                new JobParametersBuilder();


        jobParametersBuilder.addString(fileParameterName,

                message.getPayload().getAbsolutePath());

        return new JobLaunchRequest(job, jobParametersBuilder.toJobParameters());

    }
}