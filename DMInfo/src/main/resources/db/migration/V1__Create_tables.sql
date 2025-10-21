-- -----------------------------------------------------
-- Table Usuario
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Usuario (
                                       Id_usuario SERIAL PRIMARY KEY,
                                       Nome VARCHAR(80) NULL,
    Senha VARCHAR(30) NULL,
    Usuario VARCHAR(30) NULL,
    Telefone VARCHAR(14) NULL,
    Email VARCHAR(80) NULL,
    Rua VARCHAR(100) NULL,
    Cidade VARCHAR(70) NULL,
    Bairro VARCHAR(50) NULL,
    CEP VARCHAR(9) NULL,
    UF VARCHAR(2) NULL,
    CPF VARCHAR(14) NULL,
    DtNasc DATE NULL,
    DtIni DATE NULL,
    DtFim DATE NULL
    );

-- -----------------------------------------------------
-- Table Administrador
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Administrador (
                                             Id_admin SERIAL PRIMARY KEY,
                                             Id_usuario INT NULL,
                                             DtIni DATE NULL,
                                             DtFim DATE NULL,
                                             CONSTRAINT fk_Administrador_Usuario
                                             FOREIGN KEY (Id_usuario)
    REFERENCES Usuario (Id_usuario)
    );

CREATE INDEX idx_Administrador_Id_usuario ON Administrador (Id_usuario ASC);

-- -----------------------------------------------------
-- Table Membro
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Membro (
                                      Id_membro SERIAL PRIMARY KEY,
                                      Id_usuario INT NULL,
                                      DtIni DATE NULL,
                                      DtFim DATE NULL,
                                      Observacao VARCHAR(150) NULL,
    CONSTRAINT fk_Membro_Usuario
    FOREIGN KEY (Id_usuario)
    REFERENCES Usuario (Id_usuario)
    );

CREATE INDEX idx_Membro_Id_usuario ON Membro (Id_usuario ASC);

-- -----------------------------------------------------
-- Table Evento
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Evento (
                                      Id_evento SERIAL PRIMARY KEY,
                                      Id_admin INT NULL,
                                      Titulo VARCHAR(50) NULL,
    Descricao VARCHAR(50) NULL,
    CONSTRAINT fk_Evento_Administrador
    FOREIGN KEY (Id_admin)
    REFERENCES Administrador (Id_admin)
    );

CREATE INDEX idx_Evento_Id_admin ON Evento (Id_admin ASC);

-- -----------------------------------------------------
-- Table Atividade
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Atividade (
                                         Id_atividade SERIAL PRIMARY KEY,
                                         Id_evento INT NULL,
                                         Descricao VARCHAR(50) NULL,
    CONSTRAINT fk_Atividade_Evento
    FOREIGN KEY (Id_evento)
    REFERENCES Evento (Id_evento)
    );

CREATE INDEX idx_Atividade_Id_evento ON Atividade (Id_evento ASC);

-- -----------------------------------------------------
-- Table Doador
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Doador (
                                      Id_Doador SERIAL PRIMARY KEY,
                                      Nome VARCHAR(80) NULL,
    Documento VARCHAR(18) NULL,
    Rua VARCHAR(100) NULL,
    Bairro VARCHAR(50) NULL,
    Cidade VARCHAR(80) NULL,
    UF VARCHAR(2) NULL,
    CEP VARCHAR(9) NULL,
    Email VARCHAR(80) NULL,
    Telefone VARCHAR(14) NULL,
    Contato VARCHAR(80) NULL
    );

-- -----------------------------------------------------
-- Table Doacao
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Doacao (
                                      Id_Doacao INT NOT NULL PRIMARY KEY, -- Mudado para PRIMARY KEY, assumindo que não é auto-increment
                                      Id_Doador INT NULL,
                                      Id_admin INT NULL,
                                      Data DATE NULL,
                                      Valor DECIMAL(10,2) NULL,
    Observacao VARCHAR(150) NULL,
    CONSTRAINT fk_Doacao_Administrador1
    FOREIGN KEY (Id_admin)
    REFERENCES Administrador (Id_admin)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_Doacao_Doador1
    FOREIGN KEY (Id_Doador)
    REFERENCES Doador (Id_Doador)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_Doacao_Administrador1_idx ON Doacao (Id_admin ASC);
CREATE INDEX fk_Doacao_Doador1_idx ON Doacao (Id_Doador ASC);

-- -----------------------------------------------------
-- Table Recurso
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Recurso (
                                       Id_recurso SERIAL PRIMARY KEY,
                                       Id_Doacao INT NULL,
                                       Descricao VARCHAR(50) NULL,
    Tipo VARCHAR(40) NULL,
    Quantidade INT NULL,
    CONSTRAINT fk_Recurso_Doacao1
    FOREIGN KEY (Id_Doacao)
    REFERENCES Doacao (Id_Doacao)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_Recurso_Doacao1_idx ON Recurso (Id_Doacao ASC);

-- -----------------------------------------------------
-- Table Mensalidade
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Mensalidade (
                                           Id_mensalidade SERIAL PRIMARY KEY,
                                           Id_membro INT NULL,
                                           Mes INT NULL,
                                           Ano INT NULL,
                                           Valor DECIMAL(10,2) NULL,
    DataPagamento DATE NULL,
    CONSTRAINT fk_Mensalidade_Membro1
    FOREIGN KEY (Id_membro)
    REFERENCES Membro (Id_membro)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_Mensalidade_Membro1_idx ON Mensalidade (Id_membro ASC);

-- -----------------------------------------------------
-- Table Conquista
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Conquista (
                                         Id_conquista SERIAL PRIMARY KEY,
                                         Descricao VARCHAR(50) NULL
    );

-- -----------------------------------------------------
-- Table CriarRealizacaoAtividades (Nome corrigido)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS CriarRealizacaoAtividades (
                                                         Id_Criacao INT NOT NULL PRIMARY KEY, -- Mudado para PRIMARY KEY
                                                         Id_admin INT NULL,
                                                         Id_atividade INT NULL,
                                                         Horario TIME NULL,
                                                         Local VARCHAR(100) NULL,
    Observacoes VARCHAR(150) NULL,
    DtIni DATE NULL,
    DtFim DATE NULL,
    custoPrevisto DECIMAL(10,2) NULL,
    custoReal DECIMAL(10,2) NULL,
    CONSTRAINT fk_CriarRealizacaoAtividades_Atividade1
    FOREIGN KEY (Id_atividade)
    REFERENCES Atividade (Id_atividade)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_CriarRealizacaoAtividades_Administrador1
    FOREIGN KEY (Id_admin)
    REFERENCES Administrador (Id_admin)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_CriarRealizacaoAtividades_Atividade1_idx ON CriarRealizacaoAtividades (Id_atividade ASC);
CREATE INDEX fk_CriarRealizacaoAtividades_Administrador1_idx ON CriarRealizacaoAtividades (Id_admin ASC);

-- -----------------------------------------------------
-- Table EnviarFotosAtividade
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS EnviarFotosAtividade (
                                                    Id_Foto INT NOT NULL PRIMARY KEY, -- Mudado para PRIMARY KEY
                                                    Id_membro INT NULL,
                                                    Id_atividade INT NULL,
                                                    Foto VARCHAR(150) NULL,
    Data DATE NULL,
    CONSTRAINT fk_EnviarFotosAtividade_Atividade1
    FOREIGN KEY (Id_atividade)
    REFERENCES Atividade (Id_atividade)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_EnviarFotosAtividade_Membro1
    FOREIGN KEY (Id_membro)
    REFERENCES Membro (Id_membro)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_EnviarFotosAtividade_Atividade1_idx ON EnviarFotosAtividade (Id_atividade ASC);
CREATE INDEX fk_EnviarFotosAtividade_Membro1_idx ON EnviarFotosAtividade (Id_membro ASC);

-- -----------------------------------------------------
-- Table Calendario (Nome corrigido)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Calendario (
                                          Id_Calendario INT NOT NULL PRIMARY KEY, -- Mudado para PRIMARY KEY
                                          Id_Criacao INT NULL,
                                          CONSTRAINT fk_Calendario_CriarRealizacaoAtividades1
                                          FOREIGN KEY (Id_Criacao)
    REFERENCES CriarRealizacaoAtividades (Id_Criacao)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_Calendario_CriarRealizacaoAtividades1_idx ON Calendario (Id_Criacao ASC);

-- -----------------------------------------------------
-- Table NotificarAtividade
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS NotificarAtividade (
                                                  Id_Notificacao INT NOT NULL PRIMARY KEY, -- Mudado para PRIMARY KEY
                                                  Id_Calendario INT NULL,
                                                  Id_admin INT NULL,
                                                  Data DATE NULL,
                                                  Status VARCHAR(5) NULL,
    Titulo VARCHAR(100) NULL,
    Descricao VARCHAR(150) NULL,
    CONSTRAINT fk_NotificarAtividade_Calendario1
    FOREIGN KEY (Id_Calendario)
    REFERENCES Calendario (Id_Calendario)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_NotificarAtividade_Administrador1
    FOREIGN KEY (Id_admin)
    REFERENCES Administrador (Id_admin)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_NotificarAtividade_Calendario1_idx ON NotificarAtividade (Id_Calendario ASC);
CREATE INDEX fk_NotificarAtividade_Administrador1_idx ON NotificarAtividade (Id_admin ASC);

-- -----------------------------------------------------
-- Table AtribuirConquistaMembro
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AtribuirConquistaMembro (
                                                       Id_AtribuirConquista INT NOT NULL PRIMARY KEY, -- Mudado para PRIMARY KEY
                                                       Id_admin INT NULL,
                                                       Id_membro INT NULL,
                                                       Id_conquista INT NULL,
                                                       Data DATE NULL,
                                                       Observacao VARCHAR(150) NULL,
    CONSTRAINT fk_AtribuirConquistaMembro_Conquista1
    FOREIGN KEY (Id_conquista)
    REFERENCES Conquista (Id_conquista)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_AtribuirConquistaMembro_Membro1
    FOREIGN KEY (Id_membro)
    REFERENCES Membro (Id_membro)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_AtribuirConquistaMembro_Administrador1
    FOREIGN KEY (Id_admin)
    REFERENCES Administrador (Id_admin)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_AtribuirConquistaMembro_Conquista1_idx ON AtribuirConquistaMembro (Id_conquista ASC);
CREATE INDEX fk_AtribuirConquistaMembro_Membro1_idx ON AtribuirConquistaMembro (Id_membro ASC);
CREATE INDEX fk_AtribuirConquistaMembro_Administrador1_idx ON AtribuirConquistaMembro (Id_admin ASC);

-- -----------------------------------------------------
-- Table Parametros
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Parametros (
                                          ID_Parametro INT NOT NULL PRIMARY KEY, -- Mudado para PRIMARY KEY
                                          Razao_Social VARCHAR(100) NULL, -- Removido espaço
    Nome_Fantasia VARCHAR(100) NULL, -- Removido espaço
    Descricao VARCHAR(150) NULL,
    Rua VARCHAR(100) NULL,
    Bairro VARCHAR(50) NULL,
    Cidade VARCHAR(70) NULL,
    CEP VARCHAR(9) NULL,
    UF VARCHAR(2) NULL,
    Telefone VARCHAR(14) NULL,
    Site VARCHAR(80) NULL,
    Email VARCHAR(80) NULL,
    CNPJ VARCHAR(18) NULL,
    LogoTipoGrande VARCHAR(150) NULL,
    LogoTipoPequeno VARCHAR(150) NULL
    );

-- -----------------------------------------------------
-- Table CriarRealizacaoAtividades_Recurso
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS CriarRealizacaoAtividades_Recurso (
                                                                 CriarRealizacaoAtividades_Id_Criacao INT NOT NULL,
                                                                 Recurso_Id_recurso INT NOT NULL,
                                                                 Quantidade INT NULL,
                                                                 PRIMARY KEY (CriarRealizacaoAtividades_Id_Criacao, Recurso_Id_recurso),
    CONSTRAINT fk_CriarRealizacaoAtividades_has_Recurso_CriarRealizacaoA1
    FOREIGN KEY (CriarRealizacaoAtividades_Id_Criacao)
    REFERENCES CriarRealizacaoAtividades (Id_Criacao)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_CriarRealizacaoAtividades_has_Recurso_Recurso1
    FOREIGN KEY (Recurso_Id_recurso)
    REFERENCES Recurso (Id_recurso)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_CriarRealizacaoAtividades_has_Recurso_Recurso1_idx ON CriarRealizacaoAtividades_Recurso (Recurso_Id_recurso ASC);
CREATE INDEX fk_CriarRealizacaoAtividades_has_Recurso_CriarRealizaca_idx ON CriarRealizacaoAtividades_Recurso (CriarRealizacaoAtividades_Id_Criacao ASC);

-- -----------------------------------------------------
-- Table NotificarAtividade_Membro
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS NotificarAtividade_Membro (
                                                         NotificarAtividade_Id_Notificacao INT NOT NULL,
                                                         Membro_Id_membro INT NOT NULL,
                                                         PRIMARY KEY (NotificarAtividade_Id_Notificacao, Membro_Id_membro),
    CONSTRAINT fk_NotificarAtividade_has_Membro_NotificarAtividade1
    FOREIGN KEY (NotificarAtividade_Id_Notificacao)
    REFERENCES NotificarAtividade (Id_Notificacao)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_NotificarAtividade_has_Membro_Membro1
    FOREIGN KEY (Membro_Id_membro)
    REFERENCES Membro (Id_membro)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_NotificarAtividade_has_Membro_Membro1_idx ON NotificarAtividade_Membro (Membro_Id_membro ASC);
CREATE INDEX fk_NotificarAtividade_has_Membro_NotificarAtividade1_idx ON NotificarAtividade_Membro (NotificarAtividade_Id_Notificacao ASC);

-- -----------------------------------------------------
-- Table CriarRealizacaoAtividades_Membro
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS CriarRealizacaoAtividades_Membro (
                                                                CriarRealizacaoAtividades_Id_Criacao INT NOT NULL,
                                                                Membro_Id_membro INT NOT NULL,
                                                                StatusFrequencia VARCHAR(5) NULL,
    PRIMARY KEY (CriarRealizacaoAtividades_Id_Criacao, Membro_Id_membro),
    CONSTRAINT fk_CriarRealizacaoAtividades_has_Membro_CriarRealizacaoAt1
    FOREIGN KEY (CriarRealizacaoAtividades_Id_Criacao)
    REFERENCES CriarRealizacaoAtividades (Id_Criacao)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_CriarRealizaAtividades_has_Membro_Membro1
    FOREIGN KEY (Membro_Id_membro)
    REFERENCES Membro (Id_membro)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_CriarRealizacaoAtividades_has_Membro_Membro1_idx ON CriarRealizacaoAtividades_Membro (Membro_Id_membro ASC);
CREATE INDEX fk_CriarRealizacaoAtividades_has_Membro_CriarRealizacao_idx ON CriarRealizacaoAtividades_Membro (CriarRealizacaoAtividades_Id_Criacao ASC);

-- -----------------------------------------------------
-- Table DistribuicaoDeRecursos
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS DistribuicaoDeRecursos (
                                                      Id_Distribuicao INT NOT NULL PRIMARY KEY, -- Mudado para PRIMARY KEY
                                                      Id_admin INT NULL,
                                                      Data DATE NULL,
                                                      Descricao VARCHAR(50) NULL,
    InstituicaoReceptora VARCHAR(100) NULL,
    Valor DECIMAL(10,2) NULL,
    CONSTRAINT fk_DistribuicaoDeRecursos_Administrador1
    FOREIGN KEY (Id_admin)
    REFERENCES Administrador (Id_admin)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_DistribuicaoDeRecursos_Administrador1_idx ON DistribuicaoDeRecursos (Id_admin ASC);

-- -----------------------------------------------------
-- Table Recurso_has_DistribuicaoDeRecursos
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Recurso_has_DistribuicaoDeRecursos (
                                                                  Recurso_Id_recurso INT NOT NULL,
                                                                  DistribuicaoDeRecursos_Id_Distribuicao INT NOT NULL,
                                                                  Quantidade INT NULL,
                                                                  PRIMARY KEY (Recurso_Id_recurso, DistribuicaoDeRecursos_Id_Distribuicao),
    CONSTRAINT fk_Recurso_has_DistribuicaoDeRecursos_Recurso1
    FOREIGN KEY (Recurso_Id_recurso)
    REFERENCES Recurso (Id_recurso)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT fk_Recurso_has_DistribuicaoDeRecursos_DistribuicaoDeRecursos1
    FOREIGN KEY (DistribuicaoDeRecursos_Id_Distribuicao)
    REFERENCES DistribuicaoDeRecursos (Id_Distribuicao)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );

CREATE INDEX fk_Recurso_has_DistribuicaoDeRecursos_DistribuicaoDeRecurso_idx ON Recurso_has_DistribuicaoDeRecursos (DistribuicaoDeRecursos_Id_Distribuicao ASC);
CREATE INDEX fk_Recurso_has_DistribuicaoDeRecursos_Recurso1_idx ON Recurso_has_DistribuicaoDeRecursos (Recurso_Id_recurso ASC);