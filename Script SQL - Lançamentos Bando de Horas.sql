SELECT
A.SINLAN SinalLancamento,
A.NUMEMP CódigoEmpresa,
A.TIPCOL TipoColaborador,
A.NUMCAD Matricula,
B.NOMFUN Nome,
A.CODBHR CódigoBancoHoras,
A.DATLAN DataLancamento,
A.CODSIT CódigoSituacao,
A.ORILAN OrigemLancamento,
A.QTDHOR QtdMinutos,
A.DATCMP DataCompetencia,
C.CODESC CódigoEscala,
(D.HORSEM/60) HorasSemanais
FROM R011LAN A
LEFT JOIN R034FUN (nolock) B ON B.numemp = A.numemp AND B.tipcol = A.tipcol AND B.numcad = A.numcad
LEFT JOIN R038HES (nolock) C ON C.numemp = A.numemp AND C.tipcol = A.tipcol AND C.numcad = A.numcad
LEFT JOIN R006ESC (nolock) D ON D.codesc = C.codesc
WHERE 
(C.DATALT = (SELECT MAX (DATALT) FROM R038HES TABELA001 WHERE
(TABELA001.NUMEMP = C.NUMEMP) AND
(TABELA001.TIPCOL = C.TIPCOL) AND
(TABELA001.NUMCAD = C.NUMCAD) AND
(TABELA001.DATALT <= A.DATLAN)))
