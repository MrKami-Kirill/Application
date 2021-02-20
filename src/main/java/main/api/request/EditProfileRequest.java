package main.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileRequest {

    private String name;
    private String email;
    private String password;
    private Integer removePhoto;


}
