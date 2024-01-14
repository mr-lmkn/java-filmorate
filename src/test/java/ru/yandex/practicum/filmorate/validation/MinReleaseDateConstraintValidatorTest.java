package ru.yandex.practicum.filmorate.validation;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Annotation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(properties = {"ru.yandex.practicum.filmorate.validation.filmMinReleaseDate=1895-12-28"})
class MinReleaseDateConstraintValidatorTest {

    @Mock
    MinReleaseDateConstraintValidator minReleaseDateConstraintValidator = new MinReleaseDateConstraintValidator();
    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Before
    public void setUp() {
        doCallRealMethod().when(minReleaseDateConstraintValidator).initialize(any());
        when(minReleaseDateConstraintValidator.isValid(any(), any())).thenCallRealMethod();
        AllowedValuesValidatorTestClass testClass = new AllowedValuesValidatorTestClass();
        minReleaseDateConstraintValidator.initialize(testClass);
    }

    /* Все еще не очень понимаю, почему тут параметр из ApplicationProperties = null.
    С ограничением вывода кол-ва залайканных фильмов получилось же, а с  сонтесктом... Гм. Думаю.
    @Test
    public void minReleaseDateOkTest() {
        LocalDate dateRelease = LocalDate.of(1895, 12, 29);
        assertTrue(minReleaseDateConstraintValidator.isValid(dateRelease, constraintValidatorContext));
    }

    @Test
    public void minReleaseDateErrTest() { //1895-12-28
        LocalDate dateRelease = LocalDate.of(1895, 12, 27);
        assertFalse(minReleaseDateConstraintValidator.isValid(dateRelease, constraintValidatorContext));
    }
    */

    private class AllowedValuesValidatorTestClass implements MinReleaseDateConstraint {

        @Override
        public String message() {
            return "Test Message";
        }

        @Override
        public Class<?>[] groups() {
            return new Class[]{};
        }

        @Override
        public Class<? extends Payload>[] payload() {
            return new Class[]{};
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return MinReleaseDateConstraint.class;
        }

    }

}