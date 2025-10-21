-- -----------------------------------------------------
-- Table usuario
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
                                       id_usuario SERIAL PRIMARY KEY,
                                       nome VARCHAR(80) NULL,
    senha VARCHAR(30) NULL,
    usuario VARCHAR(30) NULL,
    telefone VARCHAR(14) NULL,
    email VARCHAR(80) NULL,
    rua VARCHAR(100) NULL,
    cidade VARCHAR(70) NULL,
    bairro VARCHAR(50) NULL,
    cep VARCHAR(9) NULL,
    uf VARCHAR(2) NULL,
    cpf VARCHAR(14) NULL,
    dtnasc DATE NULL,
    dtini DATE NULL,
    dtfim DATE NULL
    );

-- -----------------------------------------------------
-- Table administrador
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS administrador (
                                             id_admin SERIAL PRIMARY KEY,
                                             id_usuario INT NULL,
                                             dtini DATE NULL,
                                             dtfim DATE NULL,
                                             CONSTRAINT fk_administrador_usuario
                                             FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario)
    );

CREATE INDEX idx_administrador_id_usuario ON administrador (id_usuario ASC);

-- -----------------------------------------------------
-- Table membro
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS membro (
                                      id_membro SERIAL PRIMARY KEY,
                                      id_usuario INT NULL,
                                      dtini DATE NULL,
                                      dtfim DATE NULL,
                                      observacao VARCHAR(150) NULL,
    CONSTRAINT fk_membro_usuario
    FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario)
    );

CREATE INDEX idx_membro_id_usuario ON membro (id_usuario ASC);

-- -----------------------------------------------------
-- Table evento
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS evento (
                                      id_evento SERIAL PRIMARY KEY,
                                      id_admin INT NULL,
                                      titulo VARCHAR(50) NULL,
    descricao VARCHAR(50) NULL,
    CONSTRAINT fk_evento_administrador
    FOREIGN KEY (id_admin)
    REFERENCES administrador (id_admin)
    );

CREATE INDEX idx_evento_id_admin ON evento (id_admin ASC);

-- -----------------------------------------------------
-- Table atividade
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS atividade (
                                         id_atividade SERIAL PRIMARY KEY,
                                         id_evento INT NULL,
                                         descricao VARCHAR(50) NULL,
    CONSTRAINT fk_atividade_evento
    FOREIGN KEY (id_evento)
    REFERENCES evento (id_evento)
    );

CREATE INDEX idx_atividade_id_evento ON atividade (id_evento ASC);

-- -----------------------------------------------------
-- Table doador
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS doador (
                                      id_doador SERIAL PRIMARY KEY,
                                      nome VARCHAR(80) NULL,
    documento VARCHAR(18) NULL,
    rua VARCHAR(100) NULL,
    bairro VARCHAR(50) NULL,
    cidade VARCHAR(80) NULL,
    uf VARCHAR(2) NULL,
    cep VARCHAR(9) NULL,
    email VARCHAR(80) NULL,
    telefone VARCHAR(14) NULL,
    contato VARCHAR(80) NULL
    );

-- -----------------------------------------------------
-- Table doacao
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS doacao (
                                      id_doacao INT NOT NULL PRIMARY KEY,
                                      id_doador INT NULL,
                                      id_admin INT NULL,
                                      data DATE NULL,
                                      valor double precision NULL, -- <<< ALTERADO AQUI
                                      observacao VARCHAR(150) NULL,
    CONSTRAINT fk_doacao_administrador1
    FOREIGN KEY (id_admin)
    REFERENCES administrador (id_admin)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_doacao_doador1
    FOREIGN KEY (id_doador)
    REFERENCES doador (id_doador)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_doacao_administrador1_idx ON doacao (id_admin ASC);
CREATE INDEX fk_doacao_doador1_idx ON doacao (id_doador ASC);

-- -----------------------------------------------------
-- Table recurso
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS recurso (
                                       id_recurso SERIAL PRIMARY KEY,
                                       id_doacao INT NULL,
                                       descricao VARCHAR(50) NULL,
    tipo VARCHAR(40) NULL,
    quantidade INT NULL,
    CONSTRAINT fk_recurso_doacao1
    FOREIGN KEY (id_doacao)
    REFERENCES doacao (id_doacao)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_recurso_doacao1_idx ON recurso (id_doacao ASC);

-- -----------------------------------------------------
-- Table mensalidade
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS mensalidade (
                                           id_mensalidade SERIAL PRIMARY KEY,
                                           id_membro INT NULL,
                                           mes INT NULL,
                                           ano INT NULL,
                                           valor double precision NULL, -- <<< ALTERADO AQUI
                                           datapagamento DATE NULL,
                                           CONSTRAINT fk_mensalidade_membro1
                                           FOREIGN KEY (id_membro)
    REFERENCES membro (id_membro)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_mensalidade_membro1_idx ON mensalidade (id_membro ASC);

-- -----------------------------------------------------
-- Table conquista
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS conquista (
                                         id_conquista SERIAL PRIMARY KEY,
                                         descricao VARCHAR(50) NULL
    );

-- -----------------------------------------------------
-- Table criar_realizacao_atividades
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS criar_realizacao_atividades (
                                                           id_criacao INT NOT NULL PRIMARY KEY,
                                                           id_admin INT NULL,
                                                           id_atividade INT NULL,
                                                           horario TIME NULL,
                                                           local VARCHAR(100) NULL,
    observacoes VARCHAR(150) NULL,
    dtini DATE NULL,
    dtfim DATE NULL,
    custoprevisto double precision NULL, -- <<< ALTERADO AQUI
    custoreal double precision NULL, -- <<< ALTERADO AQUI
    CONSTRAINT fk_criar_realizacao_atividades_atividade1
    FOREIGN KEY (id_atividade)
    REFERENCES atividade (id_atividade)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_criar_realizacao_atividades_administrador1
    FOREIGN KEY (id_admin)
    REFERENCES administrador (id_admin)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_criar_realizacao_atividades_atividade1_idx ON criar_realizacao_atividades (id_atividade ASC);
CREATE INDEX fk_criar_realizacao_atividades_administrador1_idx ON criar_realizacao_atividades (id_admin ASC);

-- -----------------------------------------------------
-- Table enviar_fotos_atividade
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS enviar_fotos_atividade (
                                                      id_foto INT NOT NULL PRIMARY KEY,
                                                      id_membro INT NULL,
                                                      id_atividade INT NULL,
                                                      foto VARCHAR(150) NULL,
    data DATE NULL,
    CONSTRAINT fk_enviar_fotos_atividade_atividade1
    FOREIGN KEY (id_atividade)
    REFERENCES atividade (id_atividade)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_enviar_fotos_atividade_membro1
    FOREIGN KEY (id_membro)
    REFERENCES membro (id_membro)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_enviar_fotos_atividade_atividade1_idx ON enviar_fotos_atividade (id_atividade ASC);
CREATE INDEX fk_enviar_fotos_atividade_membro1_idx ON enviar_fotos_atividade (id_membro ASC);

-- -----------------------------------------------------
-- Table calendario
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS calendario (
                                          id_calendario INT NOT NULL PRIMARY KEY,
                                          id_criacao INT NULL,
                                          CONSTRAINT fk_calendario_criar_realizacao_atividades1
                                          FOREIGN KEY (id_criacao)
    REFERENCES criar_realizacao_atividades (id_criacao)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_calendario_criar_realizacao_atividades1_idx ON calendario (id_criacao ASC);

-- -----------------------------------------------------
-- Table notificar_atividade
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS notificar_atividade (
                                                   id_notificacao INT NOT NULL PRIMARY KEY,
                                                   id_calendario INT NULL,
                                                   id_admin INT NULL,
                                                   data DATE NULL,
                                                   status VARCHAR(5) NULL,
    titulo VARCHAR(100) NULL,
    descricao VARCHAR(150) NULL,
    CONSTRAINT fk_notificar_atividade_calendario1
    FOREIGN KEY (id_calendario)
    REFERENCES calendario (id_calendario)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_notificar_atividade_administrador1
    FOREIGN KEY (id_admin)
    REFERENCES administrador (id_admin)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_notificar_atividade_calendario1_idx ON notificar_atividade (id_calendario ASC);
CREATE INDEX fk_notificar_atividade_administrador1_idx ON notificar_atividade (id_admin ASC);

-- -----------------------------------------------------
-- Table atribuir_conquista_membro
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS atribuir_conquista_membro (
                                                         id_atribuir_conquista INT NOT NULL PRIMARY KEY,
                                                         id_admin INT NULL,
                                                         id_membro INT NULL,
                                                         id_conquista INT NULL,
                                                         data DATE NULL,
                                                         observacao VARCHAR(150) NULL,
    CONSTRAINT fk_atribuir_conquista_membro_conquista1
    FOREIGN KEY (id_conquista)
    REFERENCES conquista (id_conquista)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_atribuir_conquista_membro_membro1
    FOREIGN KEY (id_membro)
    REFERENCES membro (id_membro)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_atribuir_conquista_membro_administrador1
    FOREIGN KEY (id_admin)
    REFERENCES administrador (id_admin)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_atribuir_conquista_membro_conquista1_idx ON atribuir_conquista_membro (id_conquista ASC);
CREATE INDEX fk_atribuir_conquista_membro_membro1_idx ON atribuir_conquista_membro (id_membro ASC);
CREATE INDEX fk_atribuir_conquista_membro_administrador1_idx ON atribuir_conquista_membro (id_admin ASC);

-- -----------------------------------------------------
-- Table parametros
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS parametros (
                                          id_parametro INT NOT NULL PRIMARY KEY,
                                          razao_social VARCHAR(100) NULL,
    nome_fantasia VARCHAR(100) NULL,
    descricao VARCHAR(150) NULL,
    rua VARCHAR(100) NULL,
    bairro VARCHAR(50) NULL,
    cidade VARCHAR(70) NULL,
    cep VARCHAR(9) NULL,
    uf VARCHAR(2) NULL,
    telefone VARCHAR(14) NULL,
    site VARCHAR(80) NULL,
    email VARCHAR(80) NULL,
    cnpj VARCHAR(18) NULL,
    logotipogrande VARCHAR(150) NULL,
    logotipopequeno VARCHAR(150) NULL
    );

-- -----------------------------------------------------
-- Table criar_realizacao_atividades_recurso
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS criar_realizacao_atividades_recurso (
                                                                   criar_realizacao_atividades_id_criacao INT NOT NULL,
                                                                   recurso_id_recurso INT NOT NULL,
                                                                   quantidade INT NULL,
                                                                   PRIMARY KEY (criar_realizacao_atividades_id_criacao, recurso_id_recurso),
    CONSTRAINT fk_criar_realizacao_atividades_has_recurso_criar_realizacaoa1
    FOREIGN KEY (criar_realizacao_atividades_id_criacao)
    REFERENCES criar_realizacao_atividades (id_criacao)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_criar_realizacao_atividades_has_recurso_recurso1
    FOREIGN KEY (recurso_id_recurso)
    REFERENCES recurso (id_recurso)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_criar_realizacao_atividades_has_recurso_recurso1_idx ON criar_realizacao_atividades_recurso (recurso_id_recurso ASC);
CREATE INDEX fk_criar_realizacao_atividades_has_recurso_criar_realizaca_idx ON criar_realizacao_atividades_recurso (criar_realizacao_atividades_id_criacao ASC);

-- -----------------------------------------------------
-- Table notificar_atividade_membro
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS notificar_atividade_membro (
                                                          notificar_atividade_id_notificacao INT NOT NULL,
                                                          membro_id_membro INT NOT NULL,
                                                          PRIMARY KEY (notificar_atividade_id_notificacao, membro_id_membro),
    CONSTRAINT fk_notificar_atividade_has_membro_notificar_atividade1
    FOREIGN KEY (notificar_atividade_id_notificacao)
    REFERENCES notificar_atividade (id_notificacao)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_notificar_atividade_has_membro_membro1
    FOREIGN KEY (membro_id_membro)
    REFERENCES membro (id_membro)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_notificar_atividade_has_membro_membro1_idx ON notificar_atividade_membro (membro_id_membro ASC);
CREATE INDEX fk_notificar_atividade_has_membro_notificar_atividade1_idx ON notificar_atividade_membro (notificar_atividade_id_notificacao ASC);

-- -----------------------------------------------------
-- Table criar_realizacao_atividades_membro
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS criar_realizacao_atividades_membro (
                                                                  criar_realizacao_atividades_id_criacao INT NOT NULL,
                                                                  membro_id_membro INT NOT NULL,
                                                                  statusfrequencia VARCHAR(5) NULL,
    PRIMARY KEY (criar_realizacao_atividades_id_criacao, membro_id_membro),
    CONSTRAINT fk_criar_realizacao_atividades_has_membro_criar_realizacaoat1
    FOREIGN KEY (criar_realizacao_atividades_id_criacao)
    REFERENCES criar_realizacao_atividades (id_criacao)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_criar_realiza_atividades_has_membro_membro1
    FOREIGN KEY (membro_id_membro)
    REFERENCES membro (id_membro)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_criar_realizacao_atividades_has_membro_membro1_idx ON criar_realizacao_atividades_membro (membro_id_membro ASC);
CREATE INDEX fk_criar_realizacao_atividades_has_membro_criar_realizacao_idx ON criar_realizacao_atividades_membro (criar_realizacao_atividades_id_criacao ASC);

-- -----------------------------------------------------
-- Table distribuicao_de_recursos
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS distribuicao_de_recursos (
                                                        id_distribuicao INT NOT NULL PRIMARY KEY,
                                                        id_admin INT NULL,
                                                        data DATE NULL,
                                                        descricao VARCHAR(50) NULL,
    instituicaoreceptora VARCHAR(100) NULL,
    valor double precision NULL, -- <<< ALTERADO AQUI
    CONSTRAINT fk_distribuicao_de_recursos_administrador1
    FOREIGN KEY (id_admin)
    REFERENCES administrador (id_admin)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_distribuicao_de_recursos_administrador1_idx ON distribuicao_de_recursos (id_admin ASC);

-- -----------------------------------------------------
-- Table recurso_has_distribuicao_de_recursos
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS recurso_has_distribuicao_de_recursos (
                                                                    recurso_id_recurso INT NOT NULL,
                                                                    distribuicao_de_recursos_id_distribuicao INT NOT NULL,
                                                                    quantidade INT NULL,
                                                                    PRIMARY KEY (recurso_id_recurso, distribuicao_de_recursos_id_distribuicao),
    CONSTRAINT fk_recurso_has_distribuicao_de_recursos_recurso1
    FOREIGN KEY (recurso_id_recurso)
    REFERENCES recurso (id_recurso)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_recurso_has_distribuicao_de_recursos_distribuicao_de_recursos1
    FOREIGN KEY (distribuicao_de_recursos_id_distribuicao)
    REFERENCES distribuicao_de_recursos (id_distribuicao)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_recurso_has_distribuicao_de_recursos_distribuicao_de_recurso_idx ON recurso_has_distribuicao_de_recursos (distribuicao_de_recursos_id_distribuicao ASC);
CREATE INDEX fk_recurso_has_distribuicao_de_recursos_recurso1_idx ON recurso_has_distribuicao_de_recursos (recurso_id_recurso ASC);