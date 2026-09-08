export interface AIMessage{


    role:string


    content:string


    time:string


    /**
     * 命中的意图标识，由后端返回，便于前端做差异化展示
     */
    intent?:string

}
