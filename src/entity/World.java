package entity;

import java.util.List;
import java.util.Optional;
import static entity.AppConfig.*;

//初始化各地块和各turtle
public class World {
    private Patch[][] grid;
    private List<Agent> agents;
    private List<Cop> cops;

    //参数写到main

    // 初始化 grid、agents、cops，构建邻域
    public World(int width, int height) {

    }
    // 创建 agent/cop，随机放置到空地块
    public void setup(double agentDensity, double copDensity, double k, double threshold) {

    }

    // 移动、更新状态、执法、坐牢减刑
    public void tick() {

    }

    //计算neighbor上的cops数
    //todo: 定义neighbor计算方式
    public int copsOnNeighborhood(Location location){
        int c = 0;
        //todo:计算cops
        return c;
    }

    //计算neighbor上的ActiveAgents数
    public int countActiveAgentsInNeighborhood(Location t) { ... }

    //寻找vision内叛乱分子，没有则返回null
    public Optional<Agent> getRandomActiveAgentInNeighborhood(Location location) { ... }

    // ...
}
