select  b.codfil,
		b.nomfil,
		'' + cast(b.numcgc as varchar) + '' as cnpj,
		b.codcep as cepUnidade,
		upper(i.valkey) + ' ' + upper(b.endfil) as enderecoUnidade,
		b.endnum as numeroEnderecoUnidade,
		g.nomcid as cidadeUnidade,
		g.codest as ufUnidade,
		f.nomloc as setor,
		a.nomfun, 
		a.numcad as matricula,
		a.numcpf,
		c.numcid as rg,
		d.nomdep as mae,
		upper(h.valkey) + ' ' + upper(c.endrua) as endereco,
		c.endnum as numero,
		e.nomcid as cidade,
		a.estciv as estadocivil,
		c.endcep as cep
from	r034fun (nolock) a
        inner join r030fil b on b.numemp = a.numemp and b.codfil = a.codfil 
		inner join r034cpl c on c.numemp = a.numemp and c.tipcol = a.tipcol and c.numcad = a.numcad
		left join r036dep d on d.numemp = a.numemp and d.tipcol = a.tipcol and d.numcad = a.numcad and d.grapar = 1 and d.tipsex = 'F'
		left join r074cid e on e.codcid = c.codcid
		inner join r016orn f on f.taborg = a.taborg and f.numloc = a.numloc
		left join r074cid g on g.codcid = b.codcid
		left join r996lsf (nolock) h on h.keynam = c.tiplgr and h.lstnam = 'lTiplgd'
		left join r996lsf (nolock) i on i.keynam = b.tiplgr and i.lstnam = 'lTiplgd'
where	a.tipcol = 1
and		a.sitafa not in (7,18)
and    a.numemp < 5
order  by a.numemp, b.codfil, a.nomfun


--unidade, setor (o nomes), nome colab, mat, cpf, rg, nome da mãe, endereço e estado civílcivíl
--Endereço - cep - número - uf das unidades, data de nascimento e estado civil 
