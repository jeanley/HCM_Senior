ALTER VIEW dbo.USU_VHIECOL
AS  
WITH HieAtiva AS  
(  
    SELECT  
        z.estpos,  
        z.codthp,  
        z.revhie,  
        z.postra,  
        z.pospos,  
        z.idepos,  
        parentIdepos = z.idepos - 1,  
        parentPos    = LEFT(z.pospos, LEN(z.pospos) - 2)  
    FROM dbo.r017hie z WITH (NOLOCK)  
    INNER JOIN dbo.r017rhp r WITH (NOLOCK)  
        ON  r.estpos = z.estpos  
        AND r.codthp = z.codthp  
        AND r.revhie = z.revhie  
        AND r.datfim = '1900-12-31T00:00:00.000'  
    WHERE z.estpos = 2  
),  
FunFiltrada AS  
(  
    -- Ativos (ou não afastados 7,18)  
    SELECT *  
    FROM dbo.r034fun a WITH (NOLOCK)  
    WHERE a.sitafa NOT IN (7,18)  
    UNION ALL  
    -- Afastados (7,18) mas recentes  
    SELECT *  
    FROM dbo.r034fun a WITH (NOLOCK)  
    WHERE a.sitafa IN (7,18)  
      AND a.datafa > DATEADD(DAY, -60, GETDATE())  
)  
SELECT  
      a.numemp                                   AS codigoEmpresaColaborador  
    , h.apeemp + ' - ' + h.nomemp                AS empresaColaborador  
    , a.tipcol                                   AS tipoColaborador  
    , a.numcad                                   AS matriculaColaborador  
    , a.nomfun                                   AS nomeColaborador  
    , a.postra                                   AS postoColaborador  
    , a.codcar                                   AS codigoCargoColaborador  
    , b.titcar                                   AS cargoColaborador  
    , a.codfil                                   AS codigoFilialColaborador  
    , g.nomfil                                   AS filialColaborador  
    , c.pospos                                   AS posicaoColaborador  
    , d.pospos                                   AS posicaoGestor  
    , d.postra                                   AS postoGestor  
    , e.numemp                                   AS empresaGestor  
    , e.tipcol                                   AS tipoGestor  
    , e.numcad                                   AS matriculaGestor  
    , e.nomfun                                   AS nomeGestor  
    , i.emacom                                   AS emailGestor  
    , e.codcar                                   AS codigoCargoGestor  
    , f.titcar                                   AS cargoGestor  
FROM FunFiltrada a  
LEFT JOIN dbo.r024car b WITH (NOLOCK)  
    ON  b.estcar = a.estcar  
    AND b.codcar = a.codcar  
INNER JOIN HieAtiva c  
    ON  c.estpos = a.estpos  
    AND c.postra = a.postra  
LEFT JOIN HieAtiva d  
    ON  d.estpos = c.estpos  
    AND d.pospos = c.parentPos  
    AND d.idepos = c.parentIdepos  
LEFT JOIN dbo.r034fun e WITH (NOLOCK)  
    ON  e.estpos = d.estpos  
    AND e.postra = d.postra  
    AND e.sitafa NOT IN (7,18)  
LEFT JOIN dbo.r024car f WITH (NOLOCK)  
    ON  f.estcar = e.estcar  
    AND f.codcar = e.codcar  
LEFT JOIN dbo.r030fil g WITH (NOLOCK)  
    ON  g.numemp = a.numemp  
    AND g.codfil = a.codfil  
LEFT JOIN dbo.r030emp h WITH (NOLOCK)  
    ON  h.numemp = g.numemp  
LEFT JOIN dbo.r034cpl i WITH (NOLOCK)  
    ON  i.numemp = e.numemp  
    AND i.tipcol = e.tipcol  
    AND i.numcad = e.numcad  
