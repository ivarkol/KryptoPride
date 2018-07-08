package ru.airiva.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.airiva.dto.TranslationDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranslationsRs extends RsDto{

    @JsonProperty("translationList")
    private final List<TranslationDto> translationDtos = new ArrayList<>();

    public List<TranslationDto> getTranslationDtos() {
        return translationDtos;
    }

}
