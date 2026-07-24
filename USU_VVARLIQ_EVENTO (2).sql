/* ==========================================================================
   VIEW: USU_VVARLIQ_EVENTO
   Objetivo: Análise analítica (evento a evento) da variação de proventos,
             descontos e líquido entre o mês atual e o mês anterior, por
             colaborador, com tipo de cálculo (Mensal/Adiantamento/Complementar),
             situação do colaborador, dados cadastrais (tipo de contrato,
             cargo, regional, salário) e status de tramitação do evento
             entre os dois meses.

   Cliente: Marista Brasil
   Uso pretendido: Report Generator (filtro de mês via InsClauSQLWhere) ou
                    consumo direto via Power BI / query externa.

   NOTA: a partir desta versão, o arquivo reflete a alteração manual feita
   diretamente pelo usuário no banco (envio via ALTER VIEW). Esta é a
   versão de referência para as próximas iterações.

   HISTÓRICO DE VERSÕES
   v1: comparação inicial por r046idp (líquido) e depois por evento.
   v2: corrigido casamento de mês por competência normalizada (AnoMesRef),
       pois a data de pagamento real varia (fim de semana/feriado) e
       quebrava o casamento entre meses.
   v3:
     - Trocado o LEFT JOIN entre ATU/ANT por FULL OUTER JOIN: agora eventos
       que existiram SÓ no mês anterior (e sumiram no mês atual) também
       aparecem na view — antes eram perdidos, pois a base era sempre o
       mês atual.
     - Nova coluna StatusEvento: 'Ocorreu nos dois meses' / 'Somente no
       mês atual' / 'Somente no mês anterior'.
     - Situação do colaborador agora é calculada no ÚLTIMO DIA da
       competência em análise (EOMONTH), tanto para o mês atual quanto
       para o anterior — antes usava o primeiro dia do mês.
     - Incluído campo explícito de data de pagamento real (DatPagAtual /
       DatPagAnterior), separado da competência (CompetenciaAtual /
       CompetenciaAnterior), que é a chave usada para comparação.
     - Incluída descrição do tipo de evento (Provento/Desconto/etc) via
       R996LSF (LSTNAM = 'LTipEvt').
     - Incluído nome da filial (R030FIL.NOMFIL) e apelido da empresa
       (R030EMP.APEEMP).
   v4:
     - Nova coluna SituacaoEvento ('OK' / 'Divergente' / 'Sem Base de
       Comparação'), calculada a partir do percentual de variação do
       evento. Limiar padrão: variação absoluta > 10% = Divergente
       (ajustável no CASE da coluna).
     - PercVariacaoEvento e PercVariacaoLiquido agora retornam valor
       absoluto (sem sinal negativo), via ABS().
     - Excluídos da view colaboradores cuja SituacaoAnterior (último dia
       da competência anterior) seja "Demitido".
   v5:
     - Novas colunas cadastrais: CodTipoContrato/TipoContrato (via R996LSF
       LTipCon), CargoAtual (via R024CAR), Regional (via R996LSF
       usu_lRegion, usando R030FIL.USU_REGION).
     - ValorSalarioAtual/Anterior e horas: para colaboradores Docentes
       (TIPCON 10 ou 13), somam as atividades vigentes na competência a
       partir de R171APF; para os demais, valor cadastral de R034FUN.
     - Nova coluna SituacaoAtividadeDocente.
     - PercVariacaoEvento e PercVariacaoLiquido limitados a 0,00–100,00%.
     - Performance: agregação de R171APF pré-computada por
       colaborador+competência (DISTINCT + JOIN de intervalo), em vez de
       subquery correlacionada por linha de evento.
   v6:
     - Nova coluna SalarioComposto (Atual/Anterior) para Docentes.
     - SituacaoAtividadeDocente passou a identificar o evento de Horas
       Normais por CODEVE = 1 (em vez do nome do evento).
   v7:
     - ValorSalarioAtual/Anterior para NÃO docentes passou a buscar o
       salário vigente no histórico R038HSA (mais recente DATALT/SEQALT
       até o último dia da competência), com fallback para
       R034FUN.VALSAL quando não há histórico.
   v8:
     - Renomeado HorasSemanaisAtual/Anterior para HorasMensaisAtual/
       Anterior. Para Docentes, passou a usar HorMensaisDocente
       (SUM(A.HORMES) em R171APF) em vez de HorSemDocente. Para não
       docentes, continua usando FUN.HORSEM (ver observação abaixo).
     - CORRIGIDO SalarioComposto: agora SUM((A.HORMES/60) * A.VALAPF),
       convertendo HORMES de minutos para horas antes de multiplicar
       pelo valor da hora (VALAPF) — a versão anterior multiplicava
       HORMES em minutos diretamente, inflando o valor por um fator de 60.
   v9 (esta versão):
     - HorasMensaisAtual/Anterior agora são formatadas como texto
       "horas:minutos" (ex: "184:30"), convertendo o valor de origem
       (armazenado em MINUTOS tanto em R171APF.HORMES quanto em
       R034FUN.HORSEM) com divisão inteira por 60 e resto como minutos.
       ATENÇÃO: essas colunas passaram a ser TEXTO, não mais numéricas —
       qualquer soma/média/gráfico sobre elas precisa fazer o parse de
       volta para minutos antes de agregar.
     - SalarioCompostoAtual/Anterior: para colaboradores NÃO docentes,
       agora traz o salário mensal cadastral do colaborador (mesma fonte
       de ValorSalarioAtual/Anterior — histórico R038HSA com fallback
       para R034FUN.VALSAL), em vez de NULL.

   PREMISSAS ASSUMIDAS (confirmar se necessário):
   1) R008EVC.TIPEVE: 1 e 2 = Proventos | 3 = Descontos, para o cálculo
      dos totais de proventos/descontos. A descrição textual de cada
      TIPEVE (ex: "Provento", "Desconto", "Informativo") vem de
      R996LSF WHERE LSTNAM = 'LTipEvt' AND KEYNAM = TIPEVE.
   2) Situação do colaborador resolvida via histórico de afastamento
      (R038AFA + R010SIT), vigente no ÚLTIMO DIA da competência
      (EOMONTH). Se não houver afastamento vigente nessa data,
      considera-se "Ativo".
   3) A view NÃO contém ORDER BY (SQL Server não permite ORDER BY em
      view sem TOP, e o Report Generator/CBDS normalmente descarta essa
      cláusula). A ordenação por filial e nome do colaborador deve ser
      aplicada na consulta externa (exemplo de uso ao final do arquivo).
   4) Docente = R034FUN.TIPCON IN (10,13), com base no padrão já usado em
      outras regras LSP do ambiente (ex: Regra 117 - Seguro de Vida,
      Regra 147 - Mens. Sindical Prof.) que testam essa mesma condição
      para acionar a lógica de atividades (R171APF).
   5) Evento "Horas Normais": a comparação de SituacaoAtividadeDocente
      identifica esse evento por CODEVE = 1. Confirmar se esse é de fato
      o código do evento de Horas Normais no seu ambiente (código de
      evento pode variar por tabela de eventos/empresa — se o CODEVE de
      Horas Normais não for universalmente "1" em todas as tabelas de
      evento usadas, ajustar essa constante ou incluir também o TABEVE).
   6) Salário de NÃO docentes: via histórico R038HSA (DATALT/SEQALT mais
      recente até o último dia da competência), com fallback para
      R034FUN.VALSAL quando não há histórico.
   7) ATENÇÃO — HorasMensaisAtual/Anterior mistura duas granularidades
      diferentes conforme o tipo de colaborador: para Docentes vem de
      R171APF.HORMES (carga MENSAL somada das atividades), mas para
      não-docentes continua vindo de R034FUN.HORSEM (carga SEMANAL
      cadastral, sem conversão para mensal) — ambas agora só formatadas
      como "horas:minutos", sem alterar essa diferença de base. Ou seja,
      mesmo em texto, a coluna "HorasMensais" de um não-docente ainda
      representa uma carga SEMANAL. Ainda pendente de confirmação se
      você quer converter HORSEM para equivalente mensal.
   8) HorasMensaisAtual/Anterior são colunas de TEXTO (formato "H:MM"),
      não numéricas — para somar, comparar ou usar em gráfico é preciso
      fazer o parse de volta para minutos/horas na consulta externa.

   RECOMENDAÇÕES DE PERFORMANCE (para o DBA/responsável pelo ambiente):
   - Garantir índices em: R046VER (NUMEMP,TIPCOL,NUMCAD,CODCAL),
     R044CAL (NUMEMP,CODCAL), R171APF (NUMEMP,TIPCOL,NUMCAD,DATINI,DATFIM),
     R038AFA (NUMEMP,TIPCOL,NUMCAD,DATAFA), R046IDP (NUMEMP,TIPCOL,NUMCAD,DATPAG),
     R038HSA (NUMEMP,TIPCOL,NUMCAD,DATALT,SEQALT).
   - Sempre filtrar por NUMEMP e por CompetenciaAtual (ou intervalo) na
     consulta externa — a view não tem filtro de data fixo, então uma
     consulta sem WHERE varre toda a base histórica de eventos.
   - Evitar "SELECT *" em produção; selecionar apenas as colunas
     necessárias reduz I/O especialmente pela quantidade de LEFT JOINs.
   ========================================================================== */

ALTER VIEW dbo.USU_VVARLIQ_EVENTO
AS
SELECT
    EVO.NUMEMP,
    EVO.TIPCOL,
    EVO.NUMCAD,
    FUN.NOMFUN,
    FUN.CODFIL,
    FIL.NOMFIL,
    EMP.APEEMP,

    -- Dados cadastrais do colaborador
    FUN.TIPCON                                           AS CodTipoContrato,
    TPC.VALKEY                                           AS TipoContrato,
    CAR.TITCAR                                           AS CargoAtual,
    REG.VALKEY                                           AS Regional,

    -- Salário e carga horária: para Docentes (TIPCON 10/13), soma das
    -- atividades vigentes na competência (R171APF); para os demais,
    -- valor cadastral corrente de R034FUN (ver observação de limitação
    -- no cabeçalho do arquivo).
    CASE WHEN FUN.TIPCON IN (10,13) THEN DOC_ATU.ValSalDocente ELSE ISNULL(SALATU.VALSAL, FUN.VALSAL) END   AS ValorSalarioAtual,
    CASE WHEN FUN.TIPCON IN (10,13) THEN DOC_ANT.ValSalDocente ELSE ISNULL(SALANT.VALSAL, FUN.VALSAL) END   AS ValorSalarioAnterior,

    -- Horas mensais em formato horas:minutos (o campo de origem, tanto em
    -- R171APF.HORMES quanto em R034FUN.HORSEM, é armazenado em MINUTOS)
    CASE
        WHEN FUN.TIPCON IN (10,13)
            THEN CAST(ISNULL(DOC_ATU.HorMensaisDocente,0)/60 AS VARCHAR)
                 + ':' + RIGHT('0' + CAST(ISNULL(DOC_ATU.HorMensaisDocente,0)%60 AS VARCHAR), 2)
        ELSE CAST(ISNULL(FUN.HORSEM,0)/60 AS VARCHAR)
                 + ':' + RIGHT('0' + CAST(ISNULL(FUN.HORSEM,0)%60 AS VARCHAR), 2)
    END                                                   AS HorasMensaisAtual,
    CASE
        WHEN FUN.TIPCON IN (10,13)
            THEN CAST(ISNULL(DOC_ANT.HorMensaisDocente,0)/60 AS VARCHAR)
                 + ':' + RIGHT('0' + CAST(ISNULL(DOC_ANT.HorMensaisDocente,0)%60 AS VARCHAR), 2)
        ELSE CAST(ISNULL(FUN.HORSEM,0)/60 AS VARCHAR)
                 + ':' + RIGHT('0' + CAST(ISNULL(FUN.HORSEM,0)%60 AS VARCHAR), 2)
    END                                                   AS HorasMensaisAnterior,

    -- Salário composto: para Docentes, valor calculado a partir das horas
    -- lançadas nas atividades vigentes em R171APF, SUM((HORMES/60) * VALAPF)
    -- por atividade (HORMES convertido de minutos para horas antes de
    -- multiplicar pelo valor da hora). Para NÃO docentes, traz o salário
    -- mensal cadastral do colaborador (mesma fonte de ValorSalarioAtual/
    -- Anterior — histórico R038HSA com fallback para R034FUN.VALSAL).
    CASE WHEN FUN.TIPCON IN (10,13) THEN DOC_ATU.SalarioComposto ELSE ISNULL(SALATU.VALSAL, FUN.VALSAL) END       AS SalarioCompostoAtual,
    CASE WHEN FUN.TIPCON IN (10,13) THEN DOC_ANT.SalarioComposto ELSE ISNULL(SALANT.VALSAL, FUN.VALSAL) END       AS SalarioCompostoAnterior,

    -- Situação da atividade docente: só é preenchida na própria linha do
    -- evento de Horas Normais (CODEVE = 1) de colaboradores Docentes,
    -- comparando o valor lançado no evento com o salário composto
    -- (horas x valor hora) calculado a partir das atividades vigentes na
    -- competência atual (tolerância de R$ 0,01 para arredondamento)
    CASE
        WHEN FUN.TIPCON IN (10,13) AND EVO.CODEVE = 1 THEN
            CASE
                WHEN ROUND(ABS(ISNULL(EVO.ValorAtual,0) - ISNULL(DOC_ATU.SalarioComposto,0)), 2) <= 0.01 THEN 'OK'
                ELSE 'Divergente'
            END
        ELSE NULL
    END                                                   AS SituacaoAtividadeDocente,

    -- Período
    EVO.CompetenciaAtual,
    EVO.DatPagAtual,
    EVO.CompetenciaAnterior,
    EVO.DatPagAnterior,
    EVO.TIPCAL                                           AS CodTipCal,
    TCA.VALKEY                                           AS DesTipCal,

    -- Evento
    EVO.CODEVE,
    EVO.DESEVE,
    EVO.TIPEVE,
    DTE.VALKEY                                           AS DesTipEvento,

    -- Status de tramitação do evento entre os dois meses
    EVO.StatusEvento,

    -- Valores do evento
    EVO.ValorAtual,
    EVO.ValorAnterior,
    (ISNULL(EVO.ValorAtual, 0) - ISNULL(EVO.ValorAnterior, 0))  AS DifValorEvento,
    CASE
        WHEN ISNULL(EVO.ValorAnterior, 0) = 0 THEN NULL
        ELSE
            CASE
                WHEN ROUND(ABS(((ISNULL(EVO.ValorAtual,0) - EVO.ValorAnterior) / EVO.ValorAnterior) * 100), 2) > 100 THEN 100.00
                ELSE ROUND(ABS(((ISNULL(EVO.ValorAtual,0) - EVO.ValorAnterior) / EVO.ValorAnterior) * 100), 2)
            END
    END                                                   AS PercVariacaoEvento,
    -- Situação do evento: classificação baseada no percentual de variação
    -- (já limitado à faixa 0,00-100,00%).
    -- LIMIAR DE DIVERGÊNCIA: 10% (ajustar o valor "10" abaixo conforme regra de negócio)
    CASE
        WHEN ISNULL(EVO.ValorAnterior, 0) = 0 THEN 'Sem Base de Comparação'
        WHEN ROUND(ABS(((ISNULL(EVO.ValorAtual,0) - EVO.ValorAnterior) / EVO.ValorAnterior) * 100), 2) > 10 THEN 'Divergente'
        ELSE 'OK'
    END                                                   AS SituacaoEvento,

    -- Totais do período (proventos / descontos)
    TOTATU.TotalProventos                                AS TotalProventosAtual,
    TOTATU.TotalDescontos                                AS TotalDescontosAtual,
    TOTANT.TotalProventos                                AS TotalProventosAnterior,
    TOTANT.TotalDescontos                                AS TotalDescontosAnterior,

    -- Líquido (a partir de r046idp)
    IDPATU.VALLIQ                                        AS LiquidoAtual,
    ISNULL(IDPANT.VALLIQ, 0)                             AS LiquidoAnterior,
    (ISNULL(IDPATU.VALLIQ,0) - ISNULL(IDPANT.VALLIQ, 0))  AS DifLiquido,
    CASE
        WHEN ISNULL(IDPANT.VALLIQ, 0) = 0 THEN NULL
        ELSE
            CASE
                WHEN ROUND(ABS(((IDPATU.VALLIQ - IDPANT.VALLIQ) / IDPANT.VALLIQ) * 100), 2) > 100 THEN 100.00
                ELSE ROUND(ABS(((IDPATU.VALLIQ - IDPANT.VALLIQ) / IDPANT.VALLIQ) * 100), 2)
            END
    END                                                   AS PercVariacaoLiquido,

    -- Situação do colaborador no último dia de cada competência
    ISNULL(SITATU.CODSIT, 0)                             AS CodSituacaoAtual,
    ISNULL(SITATU.DESSIT, 'Ativo')                       AS SituacaoAtual,
    ISNULL(SITANT.CODSIT, 0)                             AS CodSituacaoAnterior,
    ISNULL(SITANT.DESSIT, 'Ativo')                        AS SituacaoAnterior

FROM
(
    -- Combinação (FULL OUTER JOIN) entre eventos do mês "atual" e do mês
    -- "imediatamente anterior" (por competência), preservando eventos que
    -- só existiram em um dos dois meses.
    SELECT
        COALESCE(ATU.NUMEMP, ANT.NUMEMP)                            AS NUMEMP,
        COALESCE(ATU.TIPCOL, ANT.TIPCOL)                            AS TIPCOL,
        COALESCE(ATU.NUMCAD, ANT.NUMCAD)                            AS NUMCAD,
        COALESCE(ATU.TIPCAL, ANT.TIPCAL)                            AS TIPCAL,
        COALESCE(ATU.CODEVE, ANT.CODEVE)                            AS CODEVE,
        COALESCE(ATU.DESEVE, ANT.DESEVE)                            AS DESEVE,
        COALESCE(ATU.TIPEVE, ANT.TIPEVE)                            AS TIPEVE,
        COALESCE(ATU.AnoMesRef, DATEADD(MONTH, 1, ANT.AnoMesRef))   AS CompetenciaAtual,
        COALESCE(ANT.AnoMesRef, DATEADD(MONTH, -1, ATU.AnoMesRef))  AS CompetenciaAnterior,
        ATU.DatPagReal                                              AS DatPagAtual,
        ANT.DatPagReal                                              AS DatPagAnterior,
        ATU.VALEVE                                                  AS ValorAtual,
        ANT.VALEVE                                                  AS ValorAnterior,
        CASE
            WHEN ATU.VALEVE IS NOT NULL AND ANT.VALEVE IS NOT NULL THEN 'Ocorreu nos dois meses'
            WHEN ATU.VALEVE IS NOT NULL AND ANT.VALEVE IS NULL     THEN 'Somente no mês atual'
            WHEN ATU.VALEVE IS NULL     AND ANT.VALEVE IS NOT NULL THEN 'Somente no mês anterior'
        END                                                          AS StatusEvento
    FROM
    (
        -- Eventos agregados por competência normalizada (primeiro dia do
        -- mês/ano do DATPAG). DatPagReal guarda a data de pagamento real
        -- (apenas para exibição).
        SELECT V.NUMEMP, V.TIPCOL, V.NUMCAD, C.TIPCAL,
               DATEFROMPARTS(YEAR(C.DATPAG), MONTH(C.DATPAG), 1) AS AnoMesRef,
               MIN(C.DATPAG)                                     AS DatPagReal,
               E.CODEVE, E.DESEVE, E.TIPEVE, SUM(V.VALEVE) AS VALEVE
        FROM R046VER V
        JOIN R044CAL C ON C.NUMEMP = V.NUMEMP AND C.CODCAL = V.CODCAL
        JOIN R008EVC E ON E.CODTAB = V.TABEVE AND E.CODEVE = V.CODEVE
        GROUP BY V.NUMEMP, V.TIPCOL, V.NUMCAD, C.TIPCAL,
                 DATEFROMPARTS(YEAR(C.DATPAG), MONTH(C.DATPAG), 1),
                 E.CODEVE, E.DESEVE, E.TIPEVE
    ) ATU
    FULL OUTER JOIN
    (
        SELECT V.NUMEMP, V.TIPCOL, V.NUMCAD, C.TIPCAL,
               DATEFROMPARTS(YEAR(C.DATPAG), MONTH(C.DATPAG), 1) AS AnoMesRef,
               MIN(C.DATPAG)                                     AS DatPagReal,
               E.CODEVE, E.DESEVE, E.TIPEVE, SUM(V.VALEVE) AS VALEVE
        FROM R046VER V
        JOIN R044CAL C ON C.NUMEMP = V.NUMEMP AND C.CODCAL = V.CODCAL
        JOIN R008EVC E ON E.CODTAB = V.TABEVE AND E.CODEVE = V.CODEVE
        GROUP BY V.NUMEMP, V.TIPCOL, V.NUMCAD, C.TIPCAL,
                 DATEFROMPARTS(YEAR(C.DATPAG), MONTH(C.DATPAG), 1),
                 E.CODEVE, E.DESEVE, E.TIPEVE
    ) ANT
        ON  ANT.NUMEMP = ATU.NUMEMP
        AND ANT.TIPCOL = ATU.TIPCOL
        AND ANT.NUMCAD = ATU.NUMCAD
        AND ANT.TIPCAL = ATU.TIPCAL
        AND ANT.CODEVE = ATU.CODEVE
        AND ANT.AnoMesRef = DATEADD(MONTH, -1, ATU.AnoMesRef)
) EVO

INNER JOIN R034FUN FUN
    ON  FUN.NUMEMP = EVO.NUMEMP
    AND FUN.TIPCOL = EVO.TIPCOL
    AND FUN.NUMCAD = EVO.NUMCAD

LEFT JOIN R030FIL FIL
    ON  FIL.NUMEMP = FUN.NUMEMP
    AND FIL.CODFIL = FUN.CODFIL

LEFT JOIN R030EMP EMP
    ON  EMP.NUMEMP = FUN.NUMEMP

LEFT JOIN R024CAR CAR
    ON  CAR.ESTCAR = FUN.ESTCAR
    AND CAR.CODCAR = FUN.CODCAR

LEFT JOIN R996LSF TPC
    ON  TPC.KEYNAM = FUN.TIPCON
    AND TPC.LSTNAM = 'LTipCon'

LEFT JOIN R996LSF REG
    ON  REG.KEYNAM = FIL.USU_REGION
    AND REG.LSTNAM = 'usu_lRegion'

LEFT JOIN R996LSF TCA
    ON  TCA.KEYNAM = EVO.TIPCAL
    AND TCA.LSTNAM = 'LTipCal'

LEFT JOIN R996LSF DTE
    ON  DTE.KEYNAM = EVO.TIPEVE
    AND DTE.LSTNAM = 'LTipEvt'

-- Totais de proventos/descontos do mês atual
LEFT JOIN
(
    SELECT V.NUMEMP, V.TIPCOL, V.NUMCAD,
           DATEFROMPARTS(YEAR(C.DATPAG), MONTH(C.DATPAG), 1) AS AnoMesRef,
           SUM(CASE WHEN E.TIPEVE IN (1,2) THEN V.VALEVE ELSE 0 END) AS TotalProventos,
           SUM(CASE WHEN E.TIPEVE = 3      THEN V.VALEVE ELSE 0 END) AS TotalDescontos
    FROM R046VER V
    JOIN R044CAL C ON C.NUMEMP = V.NUMEMP AND C.CODCAL = V.CODCAL
    JOIN R008EVC E ON E.CODTAB = V.TABEVE AND E.CODEVE = V.CODEVE
    GROUP BY V.NUMEMP, V.TIPCOL, V.NUMCAD,
             DATEFROMPARTS(YEAR(C.DATPAG), MONTH(C.DATPAG), 1)
) TOTATU
    ON  TOTATU.NUMEMP = EVO.NUMEMP
    AND TOTATU.TIPCOL = EVO.TIPCOL
    AND TOTATU.NUMCAD = EVO.NUMCAD
    AND TOTATU.AnoMesRef = EVO.CompetenciaAtual

-- Totais de proventos/descontos do mês anterior
LEFT JOIN
(
    SELECT V.NUMEMP, V.TIPCOL, V.NUMCAD,
           DATEFROMPARTS(YEAR(C.DATPAG), MONTH(C.DATPAG), 1) AS AnoMesRef,
           SUM(CASE WHEN E.TIPEVE IN (1,2) THEN V.VALEVE ELSE 0 END) AS TotalProventos,
           SUM(CASE WHEN E.TIPEVE = 3      THEN V.VALEVE ELSE 0 END) AS TotalDescontos
    FROM R046VER V
    JOIN R044CAL C ON C.NUMEMP = V.NUMEMP AND C.CODCAL = V.CODCAL
    JOIN R008EVC E ON E.CODTAB = V.TABEVE AND E.CODEVE = V.CODEVE
    GROUP BY V.NUMEMP, V.TIPCOL, V.NUMCAD,
             DATEFROMPARTS(YEAR(C.DATPAG), MONTH(C.DATPAG), 1)
) TOTANT
    ON  TOTANT.NUMEMP = EVO.NUMEMP
    AND TOTANT.TIPCOL = EVO.TIPCOL
    AND TOTANT.NUMCAD = EVO.NUMCAD
    AND TOTANT.AnoMesRef = EVO.CompetenciaAnterior

-- Líquido do mês atual (r046idp) — agregado por competência normalizada
LEFT JOIN
(
    SELECT NUMEMP, TIPCOL, NUMCAD,
           DATEFROMPARTS(YEAR(DATPAG), MONTH(DATPAG), 1) AS AnoMesRef,
           SUM(VALLIQ) AS VALLIQ
    FROM R046IDP
    GROUP BY NUMEMP, TIPCOL, NUMCAD,
             DATEFROMPARTS(YEAR(DATPAG), MONTH(DATPAG), 1)
) IDPATU
    ON  IDPATU.NUMEMP = EVO.NUMEMP
    AND IDPATU.TIPCOL = EVO.TIPCOL
    AND IDPATU.NUMCAD = EVO.NUMCAD
    AND IDPATU.AnoMesRef = EVO.CompetenciaAtual

-- Líquido do mês anterior (r046idp)
LEFT JOIN
(
    SELECT NUMEMP, TIPCOL, NUMCAD,
           DATEFROMPARTS(YEAR(DATPAG), MONTH(DATPAG), 1) AS AnoMesRef,
           SUM(VALLIQ) AS VALLIQ
    FROM R046IDP
    GROUP BY NUMEMP, TIPCOL, NUMCAD,
             DATEFROMPARTS(YEAR(DATPAG), MONTH(DATPAG), 1)
) IDPANT
    ON  IDPANT.NUMEMP = EVO.NUMEMP
    AND IDPANT.TIPCOL = EVO.TIPCOL
    AND IDPANT.NUMCAD = EVO.NUMCAD
    AND IDPANT.AnoMesRef = EVO.CompetenciaAnterior

-- ==========================================================================
-- ATIVIDADES DOCENTES (R171APF) — pré-agregado por colaborador+competência
-- Em vez de correlacionar uma subquery por LINHA de evento (o que rodaria a
-- mesma agregação centenas de vezes por colaborador), construímos primeiro a
-- lista DISTINCT de competências por colaborador (a partir da mesma fonte
-- usada para montar EVO) e fazemos o JOIN de intervalo de vigência UMA VEZ
-- por combinação colaborador+competência.
-- ==========================================================================
LEFT JOIN
(
    SELECT C.NUMEMP, C.TIPCOL, C.NUMCAD, C.Competencia,
           SUM(A.VALAPF)          AS ValSalDocente,
           SUM(A.HORSEM)          AS HorSemDocente,
           SUM(A.HORMES)          AS HorMensaisDocente,
           SUM((A.HORMES/60) * A.VALAPF)  AS SalarioComposto
    FROM
    (
        SELECT DISTINCT V.NUMEMP, V.TIPCOL, V.NUMCAD,
               DATEFROMPARTS(YEAR(CAL.DATPAG), MONTH(CAL.DATPAG), 1) AS Competencia
        FROM R046VER V
        JOIN R044CAL CAL ON CAL.NUMEMP = V.NUMEMP AND CAL.CODCAL = V.CODCAL
    ) C
    JOIN R171APF A
        ON  A.NUMEMP = C.NUMEMP
        AND A.TIPCOL = C.TIPCOL
        AND A.NUMCAD = C.NUMCAD
        AND A.DATINI <= EOMONTH(C.Competencia)
        AND (A.DATFIM IS NULL OR A.DATFIM = '1900-12-31' OR A.DATFIM >= C.Competencia)
    GROUP BY C.NUMEMP, C.TIPCOL, C.NUMCAD, C.Competencia
) DOC_ATU
    ON  DOC_ATU.NUMEMP = EVO.NUMEMP
    AND DOC_ATU.TIPCOL = EVO.TIPCOL
    AND DOC_ATU.NUMCAD = EVO.NUMCAD
    AND DOC_ATU.Competencia = EVO.CompetenciaAtual

LEFT JOIN
(
    SELECT C.NUMEMP, C.TIPCOL, C.NUMCAD, C.Competencia,
           SUM(A.VALAPF)          AS ValSalDocente,
           SUM(A.HORSEM)          AS HorSemDocente,
           SUM(A.HORMES)          AS HorMensaisDocente,
           SUM((A.HORMES/60) * A.VALAPF)  AS SalarioComposto
    FROM
    (
        SELECT DISTINCT V.NUMEMP, V.TIPCOL, V.NUMCAD,
               DATEFROMPARTS(YEAR(CAL.DATPAG), MONTH(CAL.DATPAG), 1) AS Competencia
        FROM R046VER V
        JOIN R044CAL CAL ON CAL.NUMEMP = V.NUMEMP AND CAL.CODCAL = V.CODCAL
    ) C
    JOIN R171APF A
        ON  A.NUMEMP = C.NUMEMP
        AND A.TIPCOL = C.TIPCOL
        AND A.NUMCAD = C.NUMCAD
        AND A.DATINI <= EOMONTH(C.Competencia)
        AND (A.DATFIM IS NULL OR A.DATFIM = '1900-12-31' OR A.DATFIM >= C.Competencia)
    GROUP BY C.NUMEMP, C.TIPCOL, C.NUMCAD, C.Competencia
) DOC_ANT
    ON  DOC_ANT.NUMEMP = EVO.NUMEMP
    AND DOC_ANT.TIPCOL = EVO.TIPCOL
    AND DOC_ANT.NUMCAD = EVO.NUMCAD
    AND DOC_ANT.Competencia = EVO.CompetenciaAnterior

-- Situação vigente do colaborador no ÚLTIMO DIA da competência atual
OUTER APPLY
(
    SELECT TOP (1) A.SITAFA AS CODSIT, S.DESSIT
    FROM R038AFA A
    JOIN R010SIT S ON S.CODSIT = A.SITAFA
    WHERE A.NUMEMP = EVO.NUMEMP
      AND A.TIPCOL = EVO.TIPCOL
      AND A.NUMCAD = EVO.NUMCAD
      AND A.DATAFA <= EOMONTH(EVO.CompetenciaAtual)
      AND (A.DATTER IS NULL OR A.DATTER = '1900-12-31' OR A.DATTER >= EOMONTH(EVO.CompetenciaAtual))
    ORDER BY A.DATAFA DESC
) SITATU

-- Situação vigente do colaborador no ÚLTIMO DIA da competência anterior
OUTER APPLY
(
    SELECT TOP (1) A.SITAFA AS CODSIT, S.DESSIT
    FROM R038AFA A
    JOIN R010SIT S ON S.CODSIT = A.SITAFA
    WHERE A.NUMEMP = EVO.NUMEMP
      AND A.TIPCOL = EVO.TIPCOL
      AND A.NUMCAD = EVO.NUMCAD
      AND A.DATAFA <= EOMONTH(EVO.CompetenciaAnterior)
      AND (A.DATTER IS NULL OR A.DATTER = '1900-12-31' OR A.DATTER >= EOMONTH(EVO.CompetenciaAnterior))
    ORDER BY A.DATAFA DESC
) SITANT

-- Salário vigente (não-docentes) no ÚLTIMO DIA da competência atual, via
-- histórico de alteração salarial R038HSA (mais recente DATALT/SEQALT
-- até a data de referência) — substitui o valor cadastral corrente de
-- R034FUN.VALSAL, que não refletia o histórico por competência.
OUTER APPLY
(
    SELECT TOP (1) H.VALSAL
    FROM R038HSA H
    WHERE H.NUMEMP = EVO.NUMEMP
      AND H.TIPCOL = EVO.TIPCOL
      AND H.NUMCAD = EVO.NUMCAD
      AND H.DATALT <= EOMONTH(EVO.CompetenciaAtual)
    ORDER BY H.DATALT DESC, H.SEQALT DESC
) SALATU

-- Salário vigente (não-docentes) no ÚLTIMO DIA da competência anterior
OUTER APPLY
(
    SELECT TOP (1) H.VALSAL
    FROM R038HSA H
    WHERE H.NUMEMP = EVO.NUMEMP
      AND H.TIPCOL = EVO.TIPCOL
      AND H.NUMCAD = EVO.NUMCAD
      AND H.DATALT <= EOMONTH(EVO.CompetenciaAnterior)
    ORDER BY H.DATALT DESC, H.SEQALT DESC
) SALANT

-- Exclui da análise colaboradores cuja situação no ÚLTIMO DIA da competência
-- ANTERIOR era "Demitido" (comparação case-insensitive e tolerante a variações
-- como "Demitido(a)"). Ajustar o texto abaixo caso o DESSIT real seja diferente.
WHERE UPPER(ISNULL(SITANT.DESSIT, 'ATIVO')) NOT LIKE '%DEMITID%'
GO

/* ==========================================================================
   EXEMPLO DE USO (ordenação por filial e nome do colaborador aplicada aqui,
   pois a view em si não pode conter ORDER BY sem TOP)
   ========================================================================== */
SELECT *
FROM USU_VVARLIQ_EVENTO
WHERE NUMEMP = 3
  AND CompetenciaAtual = '2026-06-01'
ORDER BY CODFIL, NOMFUN;
