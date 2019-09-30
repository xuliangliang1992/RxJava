package com.example.rxjava;

import java.util.List;

import lombok.Data;
import lombok.Getter;

/**
 * @author xuliangliang
 * @date 2019-09-23
 * copyright(c) 浩鲸云计算科技股份有限公司
 */
@Data
public class Student {

    private String name;
    private List<Course> courses;

    @Data
    static class Course {
        private String courseName;

    }

}
