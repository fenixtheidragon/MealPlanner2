package backend;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MealType {
    BREAKFAST("breakfast"),
    SECOND_BREAKFAST("second breakfast"),
    LUNCH("lunch"),
    TEA("tea"),
    DINNER("dinner"),
    SUPPER("supper"),
    SNACK("snack"),
    UNCATEGORIZED("uncategorized");

    private final String code;

    public static MealType getMealTypeByInput(String input){
        for(MealType type: MealType.values()){
            if(type.getCode().equals(input)){
                return type;
            }
        }
        return MealType.UNCATEGORIZED;
    }
}