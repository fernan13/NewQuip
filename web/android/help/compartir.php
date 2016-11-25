<html>
    
    <head>
        
        <meta charset="utf-8"/>
    </head>
    
    <body>
        
        <h2>Compartir notas,listas y dibujos</h2>
        <p> 
            Puedes compartir una nota con otras personas para que
            puedan saber en que estás pensando en cualquier momento
            y lugar. 
            Por otro lado podrás utilizar aquellas aplicaciones que 
            soporten el envio de archivos pdf para poder compartirla
            ( gmail, whatsapp... )
        </p>
        
        <h4>Para compartir</h4>
        <ol>
            
            <li>Abre la aplicación Quip</li>
            <li>Toca la nota o lista que desee compartir</li>
            <li>Toca Compartir</li>
            <li>Automáticamente se desplegará el menu para compartir la nota o lista</li>
            
        </ol>
        
        <p>
            
            <span>¿Te ha sido útil este artículo?</span>
            
            <form action="#" method="post">
            
                <input type="submit" name="submit" value="Si"/>
                <input type="submit" name="submit" value="No"/>          
            </form>
            
        </p>
        
    </body>
</html>

<?php

    if ( isset ( $_POST['submit']) ) {
        
        echo '<span>Gracias por su respuesta :)</span>';
    }
?>