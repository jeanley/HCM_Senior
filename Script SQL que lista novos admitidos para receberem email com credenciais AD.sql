select r034fun.numemp, 
        r034fun.tipcol, 
        r034fun.numcad, 
        r034fun.nomfun, 
        r034fun.numcpf, 
        r034cpl.emapar, 
        r034cpl.emacom, 
        r034usu.codusu, 
        r999usu.nomusu,  
        usu_vhiecol.nomeGestor, 
        usu_vhiecol.emailGestor 
from	r034fun, r034cpl, r034usu, r999usu, usu_vhiecol 
where	(r034fun.numemp = r034cpl.numemp and r034fun.tipcol = r034cpl.tipcol and r034fun.numcad = r034cpl.numcad) 
and		 (r034fun.numemp = r034usu.numemp and r034fun.tipcol = r034usu.tipcol and r034fun.numcad = r034usu.numcad) 
and		 (r034fun.numemp = usu_vhiecol.codigoEmpresaColaborador and r034fun.tipcol = usu_vhiecol.tipoColaborador and r034fun.numcad = usu_vhiecol.matriculaColaborador) 
and		 (r034usu.codusu = r999usu.codusu)  
and		(r034fun.datadm > getDate())       
and		(r034usu.usu_enviad <>  'S') 
