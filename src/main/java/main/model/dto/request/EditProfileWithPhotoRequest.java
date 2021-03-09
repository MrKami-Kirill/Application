package main.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileWithPhotoRequest extends EditProfileRequest {

    private MultipartFile photo;

    public EditProfileWithPhotoRequest(String name, String email, String password, int removePhoto, MultipartFile photo) {
        super(name, email, password, removePhoto);
        this.photo = photo;
    }
}
