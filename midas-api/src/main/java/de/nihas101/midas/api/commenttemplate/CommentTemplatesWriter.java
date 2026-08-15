package de.nihas101.midas.api.commenttemplate;

public interface CommentTemplatesWriter {

    CommentTemplate save(final CommentTemplate template);

    void delete(final Integer id);
}
