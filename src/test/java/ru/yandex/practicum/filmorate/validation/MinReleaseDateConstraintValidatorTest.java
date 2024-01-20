package ru.yandex.practicum.filmorate.validation;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest()
class MinReleaseDateConstraintValidatorTest {

   /* @Autowired
    MinReleaseDateConstraintValidator minReleaseDateConstraintValidator;*/
    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private static Validator validator = validatorFactory.getValidator();

    @BeforeClass
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @AfterClass
    public static void close() {
        validatorFactory.close();
    }


/*
    @Test
    public void shouldSpringBootTestAnnotation_overridePropertyValues() {
        //Тут я вижу, что @Value задает значение для MinReleaseDateConstraintValidator.MIN_FILM_RELEASE_DATE;
        String firstProperty = minReleaseDateConstraintValidator.getMinReleaseDate();
        Assert.assertEquals("1895-12-28", firstProperty);
    }
*/

    @Test
    public void minReleaseDateOkTest() {
        // Но почему, когда я проверяю валидацию, MIN_FILM_RELEASE_DATE = NULL ????
        // "1895-12-28"
        LocalDate dateRelease = LocalDate.of(1895, 12, 30);
        Film film = Film.builder().releaseDate(dateRelease)
                .name("testReleaseDate")
                .build();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void minReleaseDateErrTest() { //1895-12-28
        LocalDate dateRelease = LocalDate.of(1895, 12, 27);
        Film film = Film.builder().releaseDate(dateRelease)
                .name("testReleaseDate")
                .build();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

/*
Еще такой вариант, то же не работает...

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

    //Все еще не очень понимаю, почему тут параметр из ApplicationProperties = null.
    //С ограничением вывода кол-ва залайканных фильмов получилось же, а с  сонтесктом... Гм. Думаю.

    // "1895-12-28"
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
*/

}