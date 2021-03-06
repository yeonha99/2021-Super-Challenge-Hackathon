package inti.SAhomepage.Demerit;
import java.util.List;
import java.util.Optional;
public interface DemeritRepository {

    Demerit save(Demerit demerit);
    void update(Demerit demerit);
    void delete(Demerit demerit);
    List<Demerit> findByid(int id);
    Optional<Float> sumByid(int id);
    List<Demerit> findAll();
}
