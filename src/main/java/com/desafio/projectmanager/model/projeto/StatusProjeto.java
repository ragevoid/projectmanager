package com.desafio.projectmanager.model.projeto;

public enum StatusProjeto {

    EM_ANALISE {
        @Override
        public boolean podeTransitarPara(StatusProjeto novoStatus) {
            return novoStatus == ANALISE_REALIZADA || novoStatus == CANCELADO;
        }
    },
    ANALISE_REALIZADA {
        @Override
        public boolean podeTransitarPara(StatusProjeto novoStatus) {
            return novoStatus == ANALISE_APROVADA || novoStatus == CANCELADO;
        }
    },
    ANALISE_APROVADA {
        @Override
        public boolean podeTransitarPara(StatusProjeto novoStatus) {
            return novoStatus == INICIADO || novoStatus == CANCELADO;
        }
    },
    INICIADO {
        @Override
        public boolean podeTransitarPara(StatusProjeto novoStatus) {
            return novoStatus == PLANEJADO || novoStatus == CANCELADO;
        }
    },
    PLANEJADO {
        @Override
        public boolean podeTransitarPara(StatusProjeto novoStatus) {
            return novoStatus == EM_ANDAMENTO || novoStatus == CANCELADO;
        }
    },
    EM_ANDAMENTO {
        @Override
        public boolean podeTransitarPara(StatusProjeto novoStatus) {
            return novoStatus == ENCERRADO || novoStatus == CANCELADO;
        }
    },
    ENCERRADO {
        @Override
        public boolean podeTransitarPara(StatusProjeto novoStatus) {
            return false;
        }
    },
    CANCELADO {
        @Override
        public boolean podeTransitarPara(StatusProjeto novoStatus) {
            return false;
        }
    };

    public abstract boolean podeTransitarPara(StatusProjeto novoStatus);
}