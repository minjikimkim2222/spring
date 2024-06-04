package hello.itemservice.repository;

import hello.itemservice.domain.item.Post;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {
    private Map<Long, Post> database = new HashMap<>();
    private AtomicLong idGenerator = new AtomicLong();

    public Post save(Post post){
        if (post.getId() == null){
            post.setId(idGenerator.incrementAndGet());
        }
        database.put(post.getId(), post);
        return post;
    }

    public List<Post> getPosts(){
        return database.values().stream().toList();
    }
}
