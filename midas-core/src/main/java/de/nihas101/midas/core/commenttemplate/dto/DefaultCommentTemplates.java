package de.nihas101.midas.core.commenttemplate.dto;

import de.nihas101.midas.api.commenttemplate.CommentTemplate;
import de.nihas101.midas.api.commenttemplate.CommentTemplates;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class DefaultCommentTemplates implements CommentTemplates {

    private final List<DefaultCommentTemplate> templates;

    @Override
    public List<? extends CommentTemplate> toList() {
        return templates;
    }
}
