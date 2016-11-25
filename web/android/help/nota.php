<html>
    
    <head>
        
        <meta charset="utf-8"/>
    </head>
    
    <body>
        
        <h2>Escribir o editar una nota de texto</h2>
        <p> 
            Puedes crear notas de texto en Quip y verlas o editarlas
            en cualiquier dispositivo    
        </p>
        
        <h4>Escribir una nota</h4>
        <ol>
            
            <li>Abre la aplicación Quip</li>
            <li>En la parte inferior, toca <strong>Crear nota nueva</strong></li>
            <li>Añade texto y titulo a la nota</li>
            <li>Toca Atrás cuando hayas terminado de escribir</li>
            
        </ol>
        
        <h4>Editar una nota</h4>
        <ol>
            
            <li>Abre la aplicación Quip</li>
            <li>Toca la nota que quieras editar</li>
            <li>Haz tus cambios</li>
            <li>Toca Atrás</li>
            
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