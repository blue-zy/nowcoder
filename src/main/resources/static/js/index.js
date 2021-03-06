$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 获取标题和内容
	let title = $("#recipient-name").val();
	let content = $("#message-text").val();
	// 发送异步请求
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title": title, "content": content},
		function (data) {
			// 在提示框中显示消息
			data = $.parseJSON(data);
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 如果成功则刷新页面
				if (data.code == 0) {
					window.location.reload();
				}
			}, 2000);
		}
	);
}