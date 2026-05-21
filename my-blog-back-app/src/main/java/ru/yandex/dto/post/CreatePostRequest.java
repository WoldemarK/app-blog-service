package ru.yandex.dto.post;


import lombok.Data;

import java.util.List;

@Data
public class CreatePostRequest {

    private String title;
    private String text;
    private List<String> tags;
}
