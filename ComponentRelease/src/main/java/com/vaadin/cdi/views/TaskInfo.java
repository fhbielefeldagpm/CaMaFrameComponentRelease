package com.vaadin.cdi.views;

import java.io.Serializable;

import com.vaadin.cdi.UIScoped;

import cm.core.tasks.Task;

@UIScoped
public class TaskInfo implements Serializable {
    private Task task;


    public TaskInfo() {
        this.task = null;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}