package springcrud.securityimp.Dto;



import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private int id;
    private String title;
    private String description;
    private int capacity;
    private String instructor;
    private int createdById;
    private String createdByName;
    private int enrolledCount;
    private String imagePath;
}
