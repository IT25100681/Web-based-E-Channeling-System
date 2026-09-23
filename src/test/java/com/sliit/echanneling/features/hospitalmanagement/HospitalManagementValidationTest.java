package com.sliit.echanneling.features.hospitalmanagement;

import com.sliit.echanneling.features.hospitalmanagement.dto.HospitalForm;
import com.sliit.echanneling.features.hospitalmanagement.util.InputSanitizer;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HospitalManagementValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsUnsafeHospitalCodeAndName() {
        HospitalForm form = new HospitalForm();
        form.setCode("BAD CODE!");
        form.setName("<script>alert(1)</script>");

        assertThat(validator.validate(form)).isNotEmpty();
    }

    @Test
    void acceptsValidHospitalContactDetails() {
        HospitalForm form = new HospitalForm();
        form.setCode("HOSP-KDY");
        form.setName("LankaCare Kandy");
        form.setContactNo("+94 81 234 5678");

        assertThat(validator.validate(form)).isEmpty();
    }

    @Test
    void sanitizerRemovesMarkupCharacters() {
        InputSanitizer sanitizer = new InputSanitizer();

        assertThat(sanitizer.clean(" <b>Central</b> ")).isEqualTo("bCentral/b");
    }
}
