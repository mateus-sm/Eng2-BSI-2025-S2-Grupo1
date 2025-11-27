    package com.example.dminfo.controller;

    import com.example.dminfo.dao.AdministradorDAO;
    import com.example.dminfo.dao.UsuarioDAO;
    import com.example.dminfo.model.Administrador;
    import com.example.dminfo.model.Usuario;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;

    import java.util.List;

    @Component
    public class AdministradorController {

        @Autowired
        private AdministradorDAO dao;

        @Autowired
        private UsuarioDAO usuarioDAO;

        public Administrador buscar(int id) {
            return dao.get(id);
        }

        public List<Administrador> listar() {
            return dao.get();
        }

        public Administrador salvar(Administrador administrador) {
            if (getIdUsuario(administrador.getUsuario().getId()) != null) {
                throw new RuntimeException("Usuário já é um Administrador");
            }
            if (usuarioDAO.get(administrador.getUsuario().getId()) == null) {
                throw new RuntimeException("Usuário não existente");
            }
            return dao.gravar(administrador);
        }

        public Administrador update(int id, Administrador adminDetails) {
            Administrador existente = dao.get(id);

            if (existente == null) return null;

            if (adminDetails.getDtFim() != null && adminDetails.getDtFim().isBefore(existente.getDtIni())) {
                throw new RuntimeException("A data fim não pode ser menor que a data inicial.");
            }

            existente.setDtFim(adminDetails.getDtFim());
            dao.alterar(existente);

            return existente;
        }

        public Administrador getIdUsuario(int id) {
            if (id <= 0) {
                throw new RuntimeException("O ID precisa ser válido");
            }

            return dao.getByUsuario(id);
        }

        public boolean excluir(int id) {
            return dao.excluir(id);
        }
    }
