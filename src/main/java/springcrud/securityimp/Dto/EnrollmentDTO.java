package springcrud.securityimp.Dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDTO {
    private int id;
    private int studentId;
    private String studentName;
    private int courseId;
    private String courseTitle;
    private String enrolledAt;
}
