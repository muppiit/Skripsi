/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useEffect } from "react";
import { Form, Input, Modal } from "antd";

const { TextArea } = Input;

const AddLearningMethodForm = ({ visible, onCancel, onOk, confirmLoading }) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (visible) {
      form.resetFields();
    }
  }, [visible, form]);

  const formItemLayout = {
    labelCol: { xs: { span: 24 }, sm: { span: 8 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  };

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      onOk(values);
    } catch (info) {
      console.log("Validate Failed:", info);
    }
  };

  return (
    <Modal
      title="Tambah Metode Pembelajaran"
      open={visible}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      onOk={handleOk}
      confirmLoading={confirmLoading}
      okText="Simpan"
      cancelText="Batal"
    >
      <Form {...formItemLayout} form={form}>
        <Form.Item
          label="Nama Metode Pembelajaran:"
          name="name"
          rules={[
            {
              required: true,
              message: "Silahkan isikan nama metode pembelajaran",
            },
          ]}
        >
          <Input placeholder="Nama Metode Pembelajaran" />
        </Form.Item>

        <Form.Item
          label="Deskripsi:"
          name="description"
          rules={[
            {
              required: true,
              message: "Silahkan isikan deskripsi metode pembelajaran",
            },
          ]}
        >
          <TextArea rows={4} placeholder="Deskripsi Metode Pembelajaran" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddLearningMethodForm;
