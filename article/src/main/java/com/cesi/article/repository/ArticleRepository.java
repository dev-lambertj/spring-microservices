package com.cesi.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cesi.article.model.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
