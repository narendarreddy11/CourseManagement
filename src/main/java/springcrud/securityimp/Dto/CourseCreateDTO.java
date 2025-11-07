package springcrud.securityimp.Dto;



import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateDTO {
    private String title;
    private String description;
    private int capacity;
    private String instructor;
}
