package com.reactnatjhip.developer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.reactnatjhip.developer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AdviserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Adviser.class);
        Adviser adviser1 = new Adviser();
        adviser1.setId(1L);
        Adviser adviser2 = new Adviser();
        adviser2.setId(adviser1.getId());
        assertThat(adviser1).isEqualTo(adviser2);
        adviser2.setId(2L);
        assertThat(adviser1).isNotEqualTo(adviser2);
        adviser1.setId(null);
        assertThat(adviser1).isNotEqualTo(adviser2);
    }
}
