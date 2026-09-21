package dev.paulobernardes.rankup;

import dev.paulobernardes.rankup.banco.BancoDados;
import dev.paulobernardes.rankup.comandos.HorasComando;
import dev.paulobernardes.rankup.comandos.RankUpComando;
import dev.paulobernardes.rankup.eventos.RankUpMenuListener;
import dev.paulobernardes.rankup.gerenciadores.HorasManager;
import dev.paulobernardes.rankup.gerenciadores.JogadorManager;
import dev.paulobernardes.rankup.gerenciadores.MensagemManager;
import dev.paulobernardes.rankup.gerenciadores.PermissaoManager;
import dev.paulobernardes.rankup.gerenciadores.PlaceholderManager;
import dev.paulobernardes.rankup.gerenciadores.PromocaoManager;
import dev.paulobernardes.rankup.gerenciadores.RankManager;
import dev.paulobernardes.rankup.gerenciadores.RecompensaManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class RankUp extends JavaPlugin {

    private static RankUp instancia;

    private RankManager rankManager;
    private BancoDados bancoDados;
    private JogadorManager jogadorManager;
    private PermissaoManager permissaoManager;
    private HorasManager horasManager;
    private PlaceholderManager placeholderManager;
    private MensagemManager mensagemManager;
    private RecompensaManager recompensaManager;
    private Economy economia;
    private PromocaoManager promocaoManager;

    @Override
    public void onEnable() {

        instancia = this;

        saveDefaultConfig();

        bancoDados =
                new BancoDados(this);

        bancoDados.conectar();

        rankManager =
                new RankManager(this);

        mensagemManager =
                new MensagemManager(this);

        recompensaManager =
                new RecompensaManager(this);

        jogadorManager =
                new JogadorManager(
                        this,
                        bancoDados
                );

        permissaoManager =
                new PermissaoManager(this);

        horasManager =
                new HorasManager(this);

        if (!configurarEconomia()) {

            getLogger().severe(
                    "Nenhum sistema de economia compatível com Vault foi encontrado."
            );

            getServer()
                    .getPluginManager()
                    .disablePlugin(this);

            return;
        }

        promocaoManager =
                new PromocaoManager(this);

        placeholderManager =
                new PlaceholderManager(this);

        placeholderManager.register();

        HorasComando horasComando =
                new HorasComando(this);

        getCommand("horas")
                .setExecutor(
                        horasComando
                );

        getCommand("addhoras")
                .setExecutor(
                        horasComando
                );

        getCommand("removehoras")
                .setExecutor(
                        horasComando
                );

        RankUpComando rankUpComando =
                new RankUpComando(this);

        getCommand("rankup")
                .setExecutor(
                        rankUpComando
                );

        getServer()
                .getPluginManager()
                .registerEvents(
                        new RankUpMenuListener(this),
                        this
                );

        getLogger().info(
                "================================="
        );

        getLogger().info(
                "RankUp iniciado com sucesso!"
        );

        getLogger().info(
                "Ranks carregados: "
                        + rankManager.getQuantidadeRanks()
        );

        getLogger().info(
                "Banco de dados conectado."
        );

        getLogger().info(
                "Sistema de horas conectado."
        );

        getLogger().info(
                "Sistema de economia conectado."
        );

        getLogger().info(
                "PlaceholderAPI conectado."
        );

        getLogger().info(
                "Sistema de recompensas conectado."
        );

        getLogger().info(
                "================================="
        );
    }

    private boolean configurarEconomia() {

        if (getServer()
                .getPluginManager()
                .getPlugin("Vault") == null) {

            getLogger().severe(
                    "Vault não está instalado."
            );

            return false;
        }

        RegisteredServiceProvider<Economy> provider =
                getServer()
                        .getServicesManager()
                        .getRegistration(
                                Economy.class
                        );

        if (provider == null) {

            getLogger().severe(
                    "Nenhum provedor de economia foi encontrado pelo Vault."
            );

            return false;
        }

        economia =
                provider.getProvider();

        getLogger().info(
                "Economia conectada: "
                        + economia.getName()
        );

        return economia != null;
    }

    @Override
    public void onDisable() {

        if (placeholderManager != null) {
            placeholderManager.unregister();
        }

        if (bancoDados != null) {
            bancoDados.fechar();
        }

        getLogger().info(
                "RankUp foi desativado."
        );
    }

    public static RankUp getInstancia() {
        return instancia;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public BancoDados getBancoDados() {
        return bancoDados;
    }

    public JogadorManager getJogadorManager() {
        return jogadorManager;
    }

    public PermissaoManager getPermissaoManager() {
        return permissaoManager;
    }

    public HorasManager getHorasManager() {
        return horasManager;
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public MensagemManager getMensagemManager() {
        return mensagemManager;
    }

    public RecompensaManager getRecompensaManager() {
        return recompensaManager;
    }

    public Economy getEconomia() {
        return economia;
    }

    public PromocaoManager getPromocaoManager() {
        return promocaoManager;
    }
}