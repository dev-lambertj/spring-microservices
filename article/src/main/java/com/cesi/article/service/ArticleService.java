package com.cesi.article.service;

import org.springframework.stereotype.Service;
import com.cesi.article.model.Article;
import com.cesi.article.repository.ArticleRepository;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }

    public Article updateArticle(Long id, Article updatedArticle) {
        Article existingArticle = getArticleById(id);
        if (existingArticle != null) {
            existingArticle.setTitle(updatedArticle.getTitle());
            existingArticle.setDescription(updatedArticle.getDescription());
            return articleRepository.save(existingArticle);
        }
        return null;
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
}
