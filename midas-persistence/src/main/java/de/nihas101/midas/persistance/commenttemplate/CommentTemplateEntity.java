package de.nihas101.midas.persistance.commenttemplate;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.persistance.bookings.BookingTypeConverter;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comment_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "text", nullable = false)
    private String text;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "comment_template_booking_types",
            joinColumns = @JoinColumn(name = "comment_template_id")
    )
    @Column(name = "booking_type", nullable = false)
    @Convert(converter = BookingTypeConverter.class)
    private final Set<BookingType> bookingTypes = new HashSet<>();
}
