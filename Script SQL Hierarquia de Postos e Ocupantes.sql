select a.estpos as Estrutura, a.codthp as Tipo, a.revhie as Revisao, a.postra as PostodeTrabalho, a.pospos as PosicaoArvore, a.idepos as Nivel, b.despos, b.datcri as DataCriacao, b.datext as DataExtincao, c.numemp as Empresa, c.CodFil as Filial, c.tipcol as Tipo, c.numcad as Matricula, c.nomfun as Nome from r017hie a
left join r017pos b on b.estpos = a.estpos and b.postra = a.postra
left join r034fun c on c.estpos = a.estpos and c.postra = a.postra
where a.revhie = 2
order by pospos
