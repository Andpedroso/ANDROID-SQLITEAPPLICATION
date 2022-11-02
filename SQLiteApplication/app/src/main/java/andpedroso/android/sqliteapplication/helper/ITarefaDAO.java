package andpedroso.android.sqliteapplication.helper;

import java.util.List;
import andpedroso.android.sqliteapplication.model.Tarefa;

public interface ITarefaDAO {
    public boolean salvar(Tarefa tarefa);
    public boolean atualizar(Tarefa tarefa);
    public boolean deletar(Tarefa tarefa);
    public List<Tarefa> listar();
}
