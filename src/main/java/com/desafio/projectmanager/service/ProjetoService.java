package com.desafio.projectmanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.desafio.projectmanager.dto.request.AdicionarMembrosRequestDTO;
import com.desafio.projectmanager.dto.request.AtualizarProjetoDTO;
import com.desafio.projectmanager.dto.request.ProjetoFiltroDTO;
import com.desafio.projectmanager.dto.request.ProjetoRequestDTO;
import com.desafio.projectmanager.dto.response.ProjetoDetalhesDTO;
import com.desafio.projectmanager.dto.response.ProjetoResumoDTO;
import com.desafio.projectmanager.handler.exceptions.BusinessException;
import com.desafio.projectmanager.handler.exceptions.NotFoundException;
import com.desafio.projectmanager.mapper.ProjetoMapper;
import com.desafio.projectmanager.model.membro.Atribuicao;
import com.desafio.projectmanager.model.membro.Membro;
import com.desafio.projectmanager.model.projeto.Projeto;
import com.desafio.projectmanager.model.projeto.Risco;
import com.desafio.projectmanager.model.projeto.StatusProjeto;
import com.desafio.projectmanager.repository.ProjetoRepository;
import com.desafio.projectmanager.repository.specification.ProjetoSpecification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final ProjetoMapper projetoMapper;
    private final MembroService membroService;

    private static final BigDecimal ORCAMENTO_RISCO_BAIXO_LIMITE = new BigDecimal("100000.00");
    private static final BigDecimal ORCAMENTO_RISCO_ALTO_INICIO = new BigDecimal("500000.00");
    private static final long PRAZO_RISCO_BAIXO_MESES = 3;
    private static final long PRAZO_RISCO_ALTO_MESES = 6;
    private static final long MAXIMO_MEMBROS = 10;

    public List<ProjetoResumoDTO> listarProjetos() {
        return projetoRepository.findAllByDeletedFalse().stream()
                .map(projeto -> {
                    return projetoMapper.toResumoDTO(projeto);
                })
                .collect(Collectors.toList());
    }

    public ProjetoDetalhesDTO encontrarPorId(UUID projetoId) {
        Projeto projeto = encontrarEntidadePorId(projetoId);
        return projetoMapper.toDetalhesDTO(projeto);
    }

    @Transactional
    public ProjetoDetalhesDTO criarProjeto(ProjetoRequestDTO projetoRequestDTO) {

        Membro gerente = membroService.buscarMembroPorID(projetoRequestDTO.getGerenteId());

        verificarSeFuncionario(gerente.getId());
        verificarMaximoProjetosPorMembro(gerente.getId());

        Set<UUID> membrosIdList = new HashSet<>();
        membrosIdList.add(gerente.getId());

        Projeto projeto = projetoMapper.toEntity(projetoRequestDTO);
        projeto.setGerenteId(gerente.getId());
        projeto.setMembrosIds(membrosIdList);
        projeto.setClassificacaoRisco(calcularRisco(projeto));

        Projeto projetoSalvo = projetoRepository.save(projeto);
        return projetoMapper.toDetalhesDTO(projetoSalvo);
    }

    @Transactional
    public void eliminarProjeto(UUID projetoId) {
        Projeto projeto = encontrarEntidadePorId(projetoId);
        StatusProjeto status = projeto.getStatus();
        if (status == StatusProjeto.INICIADO || status == StatusProjeto.EM_ANDAMENTO
                || status == StatusProjeto.ENCERRADO) {
            throw new BusinessException("Projeto com status '" + status + "' não pode ser excluído.");
        }
        projeto.setDeleted(true);
        projetoRepository.save(projeto);
    }

    @Transactional
    public ProjetoDetalhesDTO atualizarProjeto(UUID projetoId, AtualizarProjetoDTO dadosParaAtualizar) {
        Projeto projeto = encontrarEntidadePorId(projetoId);

        if (projeto.getStatus() == StatusProjeto.ENCERRADO || projeto.getStatus() == StatusProjeto.CANCELADO) {
            throw new BusinessException("Não é possível alterar um projeto com status " + projeto.getStatus());
        }
        if (dadosParaAtualizar.getNome() != null) {
            projeto.setNome(dadosParaAtualizar.getNome());
        }
        if (dadosParaAtualizar.getDescricao() != null) {
            projeto.setDescricao(dadosParaAtualizar.getDescricao());
        }
        if (dadosParaAtualizar.getDataInicio() != null) {
            projeto.setDataInicio(dadosParaAtualizar.getDataInicio());
        }
        if (dadosParaAtualizar.getDataFinalPrevisao() != null) {
            projeto.setDataFinalPrevisao(dadosParaAtualizar.getDataFinalPrevisao());
        }
        if (dadosParaAtualizar.getOrcamento() != null) {
            projeto.setOrcamento(dadosParaAtualizar.getOrcamento());
        }
        if (dadosParaAtualizar.getGerenteId() != null) {
            if (!dadosParaAtualizar.getGerenteId().equals(projeto.getGerenteId())) {
                Membro novoGerente = membroService.buscarMembroPorID(dadosParaAtualizar.getGerenteId());
                if (!projeto.getMembrosIds().contains(novoGerente.getId())) {
                    throw new BusinessException(
                            "Para ser gerente, o membro também deve fazer parte da equipe do projeto.");
                }
                verificarMaximoProjetosPorMembro(novoGerente.getId());
                projeto.setGerenteId(novoGerente.getId());
            }
        }
        projeto.setClassificacaoRisco(calcularRisco(projeto));
        Projeto projetoSalvo = projetoRepository.save(projeto);
        return projetoMapper.toDetalhesDTO(projetoSalvo);
    }

    @Transactional
    public ProjetoDetalhesDTO alterarStatus(UUID projetoId, StatusProjeto novoStatus) {
        Projeto projeto = encontrarEntidadePorId(projetoId);

        StatusProjeto statusAtual = projeto.getStatus();
        if (!statusAtual.podeTransitarPara(novoStatus)) {
            throw new BusinessException(
                    String.format("Não é possível alterar o status de '%s' para '%s'", statusAtual, novoStatus));
        }
        projeto.setStatus(novoStatus);

        if (novoStatus == StatusProjeto.ENCERRADO) {
            projeto.setDataFinalReal((LocalDate.now()));
        }

        Projeto projetoSalvo = projetoRepository.save(projeto);
        return projetoMapper.toDetalhesDTO(projetoSalvo);
    }

    @Transactional
    public Page<ProjetoResumoDTO> listarProjetosPorFiltro(ProjetoFiltroDTO filtros, Pageable pageable) {
        Specification<Projeto> spec = ProjetoSpecification.filterBy(filtros);
        try {
            Page<Projeto> paginaDeProjetos = projetoRepository.findAll(spec, pageable);
            if (paginaDeProjetos.isEmpty()) {
                throw new NotFoundException("Projetos não encontrados com os filtros especificados.");
            }
            return paginaDeProjetos.map(projeto -> {
                return projetoMapper.toResumoDTO(projeto);
            });
        } catch (Exception e) {
            throw new NotFoundException("Error obtendo os Projetos: " + e.getMessage());
        }
    }

    @Transactional
    public ProjetoDetalhesDTO adicionarMembros(AdicionarMembrosRequestDTO requestDTO, UUID projetoId) {
        Projeto projeto = encontrarEntidadePorId(projetoId);

        for (UUID membroId : requestDTO.getMembrosIds()) {
            verificarSeFuncionario(membroId);
            verificarMaximoProjetosPorMembro(membroId);
            projeto.getMembrosIds().add(membroId);
        }

        projetoRepository.save(projeto);
        return projetoMapper.toDetalhesDTO(projeto);
    }

    private void verificarMaximoProjetosPorMembro(UUID membroId) {
        Membro membro = membroService.buscarMembroPorID(membroId);
        List<Projeto> projetosAtivos = projetoRepository.findProjetosAtivosPorMembro(membro.getId());
        if (projetosAtivos.size() >= MAXIMO_MEMBROS) {
            throw new BusinessException("Membro " + membro.getNome() + " já está em 3 projetos ativos.");
        }
    }

    private void verificarSeFuncionario(UUID membroId) {
        Membro membro = membroService.buscarMembroPorID(membroId);
        if (membro.getAtribuicao() != Atribuicao.FUNCIONARIO) {
            throw new BusinessException("Membro " + membro.getNome() + " não é FUNCIONARIO.");
        }
    }

    private Projeto encontrarEntidadePorId(UUID projetoId) {
        Projeto projeto = projetoRepository.findByIdAndDeletedFalse(projetoId)
                .orElseThrow(() -> new NotFoundException("Projeto não encontrado com ID: " + projetoId));
        return projeto;
    }

    private Risco calcularRisco(Projeto projeto) {
        long mesesTotais = calcularMesesTotais(projeto);
        BigDecimal orcamento = projeto.getOrcamento();

        if (isRiscoAlto(mesesTotais, orcamento)) {
            return Risco.ALTO;
        }
        if (isRiscoBaixo(mesesTotais, orcamento)) {
            return Risco.BAIXO;
        }
        return Risco.MEDIO;
    }

    private long calcularMesesTotais(Projeto projeto) {
        LocalDate inicio = projeto.getDataInicio();
        LocalDate fimPrevisto = projeto.getDataFinalPrevisao();
        return ChronoUnit.MONTHS.between(inicio, fimPrevisto);
    }

    private boolean isRiscoBaixo(long mesesTotais, BigDecimal orcamento) {
        boolean prazoOk = mesesTotais <= PRAZO_RISCO_BAIXO_MESES;
        boolean orcamentoOk = orcamento.compareTo(ORCAMENTO_RISCO_BAIXO_LIMITE) <= 0;
        return prazoOk && orcamentoOk;
    }

    private boolean isRiscoAlto(long mesesTotais, BigDecimal orcamento) {
        boolean prazoOk = mesesTotais > PRAZO_RISCO_ALTO_MESES;
        boolean orcamentoOk = orcamento.compareTo(ORCAMENTO_RISCO_ALTO_INICIO) > 0;
        return prazoOk || orcamentoOk;
    }

}