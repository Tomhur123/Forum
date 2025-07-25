package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.Data;

@Data
@TableName("db_topic_type")
public class TopicType implements BaseData {
    Integer id;
    String name;
    String description;
    String color;
}
