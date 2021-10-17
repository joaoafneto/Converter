# NFA-to-DFA-Converter

Projeto criado para a disciplina de Fundamentos Teóricos da Computação com o intuito de executar e converter arquivos de autômatos compatíveis com o software JFLAP.

### Baseado no Java JDK 16/1.8.

É possivel utilizar através de linha de comando, especificando o diretório do arquivo de entrada, seguido dos parâmetros --run ou --convert.

Utilizando o --run, é possível passar um terceiro argumento que representa a sentença a ser processada.
A saída do código será uma mensagem indicando se a sentença é aceita ou não pelo autômato.

Exemplo:
java -jar afdn.jar ./automaton.jff --run 1000

Utilizando o --convert, o programa irá converter um arquivo de um Autômato Finito Não-Determinístico para um Autômato Finito Determinístico,
e salvar em um arquivo compatível com o JFLAP. O arquivo será salvo em um diretório chamado "converted" que será criado na mesma pasta do executável. É possivel fornecer um terceiro argumento que indica o nome do arquivo de saída. Caso não seja fornecido,
o nome padrão será newAutomaton.jff.

Exemplo:
java -jar afdn.jar ./automaton.jff --convert convertedAutomaton.jff
