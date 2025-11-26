package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.Artist;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewArtistRequest {
    private String name;
    private Boolean domestic;
    private String snsUrl;
    private String imageUrl;

    public Artist toNewArtistEntity() {
        return Artist.builder()
                .artistName(name)
                .isDomestic(domestic != null ? domestic : true)
                .snsUrl(snsUrl)
                .artistImageUrl(imageUrl)
                .build();
    }
}